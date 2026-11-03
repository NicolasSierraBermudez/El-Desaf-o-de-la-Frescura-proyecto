package com.frescuratotal.erp.sales.controller;

import com.frescuratotal.erp.common.AppLogger;
import com.frescuratotal.erp.common.CompanyInfo;
import com.frescuratotal.erp.common.CompanyInfoDao;
import com.frescuratotal.erp.common.DaoFactory;
import com.frescuratotal.erp.common.NumberGenerator;
import com.frescuratotal.erp.common.invoice.InvoiceContext;
import com.frescuratotal.erp.common.invoice.InvoiceDocumentGenerator;
import com.frescuratotal.erp.common.invoice.InvoiceGeneratorFactory;
import com.frescuratotal.erp.inventory.model.Lot;
import com.frescuratotal.erp.inventory.model.LotDao;
import com.frescuratotal.erp.inventory.model.LotStatus;
import com.frescuratotal.erp.sales.model.Customer;
import com.frescuratotal.erp.sales.model.CustomerDao;
import com.frescuratotal.erp.sales.model.CustomerType;
import com.frescuratotal.erp.sales.model.CustomerTypeDao;
import com.frescuratotal.erp.sales.model.Invoice;
import com.frescuratotal.erp.sales.model.InvoiceDao;
import com.frescuratotal.erp.sales.model.Promotion;
import com.frescuratotal.erp.sales.model.PromotionDao;
import com.frescuratotal.erp.sales.model.Sale;
import com.frescuratotal.erp.sales.model.SaleDao;
import com.frescuratotal.erp.sales.model.SaleDetail;
import com.frescuratotal.erp.suppliers.model.Product;
import com.frescuratotal.erp.suppliers.model.ProductDao;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Controller for the sales &amp; billing module.
 *
 * <p>Implements the "intelligent sale": only Disponible/Proximo a Vencer lots are
 * offered, FEFO (First Expire First Out) selection, automatic 50% promotion for
 * near-expiry lots, category-based IVA, and one-click electronic invoice
 * generation (PNG + PDF) via the invoice generator factory (OCP).</p>
 */
public class SalesController {

    /** Standard IVA rate (19%) applied to most categories. */
    public static final BigDecimal IVA_STANDARD = new BigDecimal("0.19");
    /** Reduced IVA rate (5%) for basic food categories. */
    public static final BigDecimal IVA_REDUCED = new BigDecimal("0.05");
    /** Default near-expiry discount if no promotion row is configured (50%). */
    public static final BigDecimal DEFAULT_NEAR_EXPIRY_DISCOUNT = new BigDecimal("0.50");

    private final SaleDao saleDao = DaoFactory.saleDao();
    private final InvoiceDao invoiceDao = DaoFactory.invoiceDao();
    private final CustomerDao customerDao = DaoFactory.customerDao();
    private final LotDao lotDao = DaoFactory.lotDao();
    private final ProductDao productDao = DaoFactory.productDao();
    private final CustomerTypeDao customerTypeDao = new CustomerTypeDao();
    private final PromotionDao promotionDao = new PromotionDao();
    private final CompanyInfoDao companyInfoDao = new CompanyInfoDao();

    // ---------------- Customers ----------------

    public int registerCustomer(Customer c) {
        return customerDao.create(c);
    }

    public List<Customer> listCustomers() {
        return customerDao.findAll();
    }

    public Customer findCustomer(int id) {
        return customerDao.findById(id).orElse(null);
    }

    public List<CustomerType> listCustomerTypes() {
        return customerTypeDao.findAll();
    }

    public List<Product> listProducts() {
        return productDao.findAll();
    }

    // ---------------- Intelligent sale ----------------

    /** @return available lots of a product, ordered FEFO (nearest expiry first). */
    public List<Lot> availableLotsFefo(int productId) {
        return lotDao.findAvailableByProductFefo(productId);
    }

    /**
     * @return the FEFO-suggested lot for a product, or null if none available.
     *
     * <p>Even though the DAO already returns lots in FEFO order, the selection is
     * re-expressed here with the Stream API and a {@link Comparator} so the FEFO
     * rule (earliest {@code expirationDate} first) is explicit and testable at the
     * business layer: {@code Comparator.comparing(Lot::getExpirationDate)}.</p>
     */
    public Lot suggestFefoLot(int productId) {
        return availableLotsFefo(productId).stream()
                .filter(lot -> lot.getQuantity() != null && lot.getQuantity().signum() > 0)
                .min(Comparator.comparing(Lot::getExpirationDate))
                .orElse(null);
    }

    // ---------------- Stream-based sales reports ----------------

    /**
     * Sums the total billed amount across every sale using
     * {@code map(Sale::getTotalAmount).reduce(...)} over the Stream API.
     */
    public BigDecimal totalSalesAmount() {
        return saleDao.findAll().stream()
                .map(Sale::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Counts how many sales are in each status name, using
     * {@link Collectors#groupingBy} + {@link Collectors#counting} with a lambda
     * classifier. Returns a report keyed by the Spanish status name.
     */
    public Map<String, Long> countSalesByStatus() {
        return saleDao.findAll().stream()
                .collect(Collectors.groupingBy(
                        sale -> sale.getStatusName() != null
                                ? sale.getStatusName() : ("Estado " + sale.getStatusId()),
                        Collectors.counting()));
    }

    /**
     * Builds a priced sale line for a lot and quantity, applying the near-expiry
     * promotion and the category IVA. Does not persist anything.
     *
     * @return the computed {@link SaleDetail}, or {@code null} if lot not found
     */
    public SaleDetail buildLine(int lotId, BigDecimal quantity) {
        Lot lot = lotDao.findById(lotId).orElse(null);
        if (lot == null) {
            return null;
        }
        BigDecimal unitPrice = lot.getSalePrice();
        BigDecimal base = unitPrice.multiply(quantity);

        // Automatic promotion for near-expiry lots.
        BigDecimal discountRate = BigDecimal.ZERO;
        if (lot.getStatusId() == LotStatus.NEAR_EXPIRY.getId()) {
            Optional<Promotion> promo =
                    promotionDao.findActiveByLotStatus(LotStatus.NEAR_EXPIRY.getId());
            discountRate = promo.map(Promotion::getDiscountRate).orElse(DEFAULT_NEAR_EXPIRY_DISCOUNT);
        }
        BigDecimal discountAmount = base.multiply(discountRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal taxable = base.subtract(discountAmount);

        BigDecimal taxRate = taxRateForProduct(lot.getProductId());
        BigDecimal taxAmount = taxable.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal lineTotal = taxable.add(taxAmount);

        SaleDetail d = new SaleDetail();
        d.setLotId(lotId);
        d.setQuantitySold(quantity);
        d.setUnitPrice(unitPrice);
        d.setDiscountRate(discountRate);
        d.setDiscountAmount(discountAmount);
        d.setTaxRate(taxRate);
        d.setTaxAmount(taxAmount);
        d.setSubtotal(base.setScale(2, RoundingMode.HALF_UP));
        d.setLineTotal(lineTotal.setScale(2, RoundingMode.HALF_UP));
        d.setLotNumber(lot.getLotNumber());
        d.setProductName(lot.getProductName());
        return d;
    }

    /**
     * Resolves the IVA rate for a product based on its category. Basic food
     * categories (Frutas, Verduras, Granos) use the reduced 5% rate; the rest use
     * the standard 19% rate.
     */
    public BigDecimal taxRateForProduct(int productId) {
        Product p = productDao.findById(productId).orElse(null);
        if (p == null) {
            return IVA_STANDARD;
        }
        int cat = p.getCategoryId();
        // Category ids from seed data: 1=Frutas, 2=Verduras, 5=Granos.
        if (cat == 1 || cat == 2 || cat == 5) {
            return IVA_REDUCED;
        }
        return IVA_STANDARD;
    }

    /**
     * Confirms a sale: computes totals, persists header + details and decrements
     * lot quantities (transactional in the DAO). The sale is left in status
     * "En Preparacion" so it appears in the logistics dispatch panel.
     *
     * @return the persisted sale (with id and number)
     */
    public Sale confirmSale(int customerId, List<SaleDetail> details, String user) {
        Sale sale = new Sale();
        sale.setSaleNumber(NumberGenerator.nextSaleNumber());
        sale.setCustomerId(customerId);
        sale.setStatusId(Sale.STATUS_IN_PREPARATION);
        sale.setRegisteredBy(user);

        sale.getDetails().addAll(details);

        // Stream API aggregation: each total is a reduce over the line items using a
        // method reference to the relevant getter (replaces four accumulator loops).
        BigDecimal subtotal = sumBy(details, SaleDetail::getSubtotal);
        BigDecimal totalDiscount = sumBy(details, SaleDetail::getDiscountAmount);
        BigDecimal totalTax = sumBy(details, SaleDetail::getTaxAmount);
        BigDecimal totalAmount = sumBy(details, SaleDetail::getLineTotal);

        sale.setSubtotal(subtotal.setScale(2, RoundingMode.HALF_UP));
        sale.setTotalDiscount(totalDiscount.setScale(2, RoundingMode.HALF_UP));
        sale.setTotalTax(totalTax.setScale(2, RoundingMode.HALF_UP));
        sale.setTotalAmount(totalAmount.setScale(2, RoundingMode.HALF_UP));

        saleDao.create(sale);
        AppLogger.info("Venta confirmada: " + sale.getSaleNumber() + " cliente=" + customerId
                + " lineas=" + details.size() + " total=" + sale.getTotalAmount());
        return sale;
    }

    /**
     * Generic Stream helper: sums a {@link BigDecimal} field extracted from each
     * line with a {@link java.util.function.Function} (passed as a method
     * reference), ignoring null values. Demonstrates functional composition.
     */
    private static BigDecimal sumBy(List<SaleDetail> details,
                                    java.util.function.Function<SaleDetail, BigDecimal> field) {
        return details.stream()
                .map(field)
                .filter(value -> value != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Sale> listSales() {
        return saleDao.findAll();
    }

    public Sale findSaleWithDetails(int saleId) {
        return saleDao.findWithDetails(saleId);
    }

    // ---------------- One-click invoicing ----------------

    /**
     * Generates the electronic invoice for a sale: persists the invoice row (with
     * a simulated CUFE hash and QR link) and writes PNG + PDF files to the
     * {@code invoices/} folder in the working directory.
     *
     * @return the generated file paths (png, pdf)
     */
    public List<Path> generateInvoice(int saleId) {
        Sale sale = saleDao.findWithDetails(saleId);
        if (sale == null) {
            throw new IllegalArgumentException("Venta no encontrada: " + saleId);
        }
        Optional<Invoice> existing = invoiceDao.findBySale(saleId);
        Invoice invoice = existing.orElseGet(() -> buildAndPersistInvoice(sale));

        Customer customer = customerDao.findById(sale.getCustomerId()).orElse(null);
        CompanyInfo company = companyInfoDao.getCompany();

        InvoiceContext ctx = buildContext(sale, invoice, customer, company);

        List<Path> outputs = new ArrayList<>();
        Path dir = Paths.get("invoices");
        for (InvoiceGeneratorFactory.Format fmt : InvoiceGeneratorFactory.Format.values()) {
            try {
                InvoiceDocumentGenerator gen = InvoiceGeneratorFactory.create(fmt);
                outputs.add(gen.generate(ctx, dir));
            } catch (Exception ex) {
                AppLogger.error("Error al generar factura " + fmt
                        + " para venta " + saleId, ex);
                throw new RuntimeException("Error al generar factura "
                        + fmt + ": " + ex.getMessage(), ex);
            }
        }
        AppLogger.info("Factura generada: " + invoice.getInvoiceNumber()
                + " (venta " + sale.getSaleNumber() + ") archivos=" + outputs.size());
        return outputs;
    }

    private Invoice buildAndPersistInvoice(Sale sale) {
        Invoice invoice = new Invoice();
        invoice.setSaleId(sale.getId());
        invoice.setInvoiceNumber(NumberGenerator.nextInvoiceNumber());
        String cufe = simulateCufe(invoice.getInvoiceNumber(), sale);
        invoice.setCufe(cufe);
        invoice.setQrLink("https://factura.frescuratotal.com/validar?cufe=" + cufe);
        invoice.setSubtotal(sale.getSubtotal());
        invoice.setTotalDiscount(sale.getTotalDiscount());
        invoice.setTaxAmount(sale.getTotalTax());
        invoice.setTotalAmount(sale.getTotalAmount());
        invoiceDao.create(invoice);
        return invoice;
    }

    private InvoiceContext buildContext(Sale sale, Invoice invoice, Customer customer,
                                        CompanyInfo company) {
        InvoiceContext ctx = new InvoiceContext();
        ctx.setCompanyName(company.getCompanyName());
        ctx.setCompanyNit(company.getNit());
        ctx.setCompanyAddress(company.getAddress());
        ctx.setCompanyCity(company.getCity());
        ctx.setCompanyPhone(company.getPhone());
        ctx.setCompanyEmail(company.getEmail());
        if (customer != null) {
            ctx.setCustomerName(customer.getName());
            ctx.setCustomerDocument(customer.getDocument());
            ctx.setCustomerAddress(customer.getAddress());
            ctx.setCustomerPhone(customer.getPhone());
        }
        ctx.setInvoiceNumber(invoice.getInvoiceNumber());
        ctx.setSaleNumber(sale.getSaleNumber());
        ctx.setCufe(invoice.getCufe());
        ctx.setQrContent(invoice.getQrLink());
        ctx.setIssueDate(invoice.getIssueDate() != null
                ? invoice.getIssueDate() : LocalDateTime.now());
        ctx.setSubtotal(sale.getSubtotal());
        ctx.setTotalDiscount(sale.getTotalDiscount());
        ctx.setTotalTax(sale.getTotalTax());
        ctx.setTotalAmount(sale.getTotalAmount());
        for (SaleDetail d : sale.getDetails()) {
            ctx.getLines().add(new InvoiceContext.Line(
                    d.getProductName(), d.getLotNumber(), d.getQuantitySold(),
                    d.getUnitPrice(), d.getDiscountAmount(), d.getTaxAmount(),
                    d.getLineTotal()));
        }
        return ctx;
    }

    /** Builds a simulated CUFE as a SHA-256 hash of key invoice fields. */
    private String simulateCufe(String invoiceNumber, Sale sale) {
        String raw = invoiceNumber + "|" + sale.getSaleNumber() + "|"
                + sale.getTotalAmount() + "|" + LocalDateTime.now();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception ex) {
            return Integer.toHexString(raw.hashCode());
        }
    }
}
