package com.frescuratotal.erp.common.invoice;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Plain data holder with everything needed to render an electronic invoice.
 *
 * <p>Using a dedicated context keeps the document generators decoupled from the
 * persistence/domain models (SRP): the sales controller fills this object and
 * the generators only read from it.</p>
 */
public class InvoiceContext {

    /** A single printed line of the invoice. */
    public static class Line {
        private final String productName;
        private final String lotNumber;
        private final BigDecimal quantity;
        private final BigDecimal unitPrice;
        private final BigDecimal discountAmount;
        private final BigDecimal taxAmount;
        private final BigDecimal lineTotal;

        public Line(String productName, String lotNumber, BigDecimal quantity,
                    BigDecimal unitPrice, BigDecimal discountAmount,
                    BigDecimal taxAmount, BigDecimal lineTotal) {
            this.productName = productName;
            this.lotNumber = lotNumber;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.discountAmount = discountAmount;
            this.taxAmount = taxAmount;
            this.lineTotal = lineTotal;
        }

        public String getProductName() {
            return productName;
        }

        public String getLotNumber() {
            return lotNumber;
        }

        public BigDecimal getQuantity() {
            return quantity;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public BigDecimal getDiscountAmount() {
            return discountAmount;
        }

        public BigDecimal getTaxAmount() {
            return taxAmount;
        }

        public BigDecimal getLineTotal() {
            return lineTotal;
        }
    }

    // Company (issuer)
    private String companyName;
    private String companyNit;
    private String companyAddress;
    private String companyCity;
    private String companyPhone;
    private String companyEmail;

    // Customer (recipient)
    private String customerName;
    private String customerDocument;
    private String customerAddress;
    private String customerPhone;

    // Invoice identity
    private String invoiceNumber;
    private String saleNumber;
    private String cufe;
    private String qrContent;
    private LocalDateTime issueDate;

    // Totals
    private BigDecimal subtotal;
    private BigDecimal totalDiscount;
    private BigDecimal totalTax;
    private BigDecimal totalAmount;

    private final List<Line> lines = new ArrayList<>();

    // Getters / setters

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyNit() {
        return companyNit;
    }

    public void setCompanyNit(String companyNit) {
        this.companyNit = companyNit;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public String getCompanyPhone() {
        return companyPhone;
    }

    public void setCompanyPhone(String companyPhone) {
        this.companyPhone = companyPhone;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerDocument() {
        return customerDocument;
    }

    public void setCustomerDocument(String customerDocument) {
        this.customerDocument = customerDocument;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public String getCufe() {
        return cufe;
    }

    public void setCufe(String cufe) {
        this.cufe = cufe;
    }

    public String getQrContent() {
        return qrContent;
    }

    public void setQrContent(String qrContent) {
        this.qrContent = qrContent;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<Line> getLines() {
        return lines;
    }
}
