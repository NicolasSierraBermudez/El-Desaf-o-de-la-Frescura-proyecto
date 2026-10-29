package com.frescuratotal.erp.sales.view;

import com.frescuratotal.erp.common.ConsoleUtils;
import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.inventory.model.Lot;
import com.frescuratotal.erp.sales.controller.SalesController;
import com.frescuratotal.erp.sales.model.Customer;
import com.frescuratotal.erp.sales.model.CustomerType;
import com.frescuratotal.erp.sales.model.Sale;
import com.frescuratotal.erp.sales.model.SaleDetail;
import com.frescuratotal.erp.suppliers.model.Product;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Console view for the sales &amp; billing module. Presentation only; business
 * rules (FEFO, promotions, IVA, invoicing) live in {@link SalesController}.
 * User-facing text is Spanish.
 */
public class SalesView {

    private final SalesController controller = new SalesController();

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== MODULO: VENTAS Y FACTURACION =====");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Nueva venta (venta inteligente FEFO)");
            System.out.println("4. Listar ventas");
            System.out.println("5. Ver detalle de una venta");
            System.out.println("6. Generar factura electronica (PNG + PDF)");
            System.out.println("0. Volver al menu principal");
            int option = ConsoleUtils.readInt("Seleccione una opcion: ", 0, 6);
            try {
                switch (option) {
                    case 1: registerCustomer(); break;
                    case 2: listCustomers(); break;
                    case 3: newSale(); break;
                    case 4: listSales(); break;
                    case 5: saleDetail(); break;
                    case 6: generateInvoice(); break;
                    case 0: back = true; break;
                    default: break;
                }
            } catch (DaoException ex) {
                System.out.println("\n[ERROR DE BASE DE DATOS] " + ex.getMessage());
                System.out.println("Verifique que MySQL este activo y la BD creada.");
                ConsoleUtils.pause();
            }
        }
    }

    private void registerCustomer() {
        System.out.println("\n--- Registro de cliente ---");
        List<CustomerType> types = controller.listCustomerTypes();
        System.out.println("Tipos de cliente:");
        for (CustomerType t : types) {
            System.out.println("  " + t);
        }
        int typeId = ConsoleUtils.readInt("Id del tipo de cliente: ");
        String name = ConsoleUtils.readRequiredString("Nombre: ");
        String document = ConsoleUtils.readRequiredString("Documento/NIT: ");
        String email = ConsoleUtils.readString("Email: ");
        String phone = ConsoleUtils.readString("Telefono: ");
        String address = ConsoleUtils.readString("Direccion: ");
        Customer c = new Customer(name, document, email, phone, address, typeId);
        int id = controller.registerCustomer(c);
        System.out.println("Cliente registrado con id " + id + ".");
        ConsoleUtils.pause();
    }

    private void listCustomers() {
        System.out.println("\n--- Clientes registrados ---");
        List<Customer> list = controller.listCustomers();
        if (list.isEmpty()) {
            System.out.println("No hay clientes registrados.");
        } else {
            for (Customer c : list) {
                System.out.println("  " + c);
            }
        }
        ConsoleUtils.pause();
    }

    private void newSale() {
        System.out.println("\n--- Nueva venta inteligente ---");
        int customerId = ConsoleUtils.readInt("Id del cliente: ");
        Customer customer = controller.findCustomer(customerId);
        if (customer == null) {
            System.out.println("Cliente no encontrado.");
            ConsoleUtils.pause();
            return;
        }
        System.out.println("Cliente: " + customer.getName());

        List<SaleDetail> cart = new ArrayList<>();
        boolean addMore = true;
        while (addMore) {
            System.out.println("\nProductos disponibles:");
            for (Product p : controller.listProducts()) {
                System.out.printf("  [%d] %s%n", p.getId(), p.getName());
            }
            int productId = ConsoleUtils.readInt("Id del producto a vender: ");
            List<Lot> lots = controller.availableLotsFefo(productId);
            if (lots.isEmpty()) {
                System.out.println("No hay lotes disponibles para este producto "
                        + "(solo se ofrecen lotes Disponibles o Proximos a Vencer).");
            } else {
                System.out.println("Lotes disponibles (orden FEFO, primero en vencer primero en salir):");
                for (Lot l : lots) {
                    System.out.printf("  [%d] %s | Vence: %s | Cant: %s | Precio: $%s | %s%n",
                            l.getId(), l.getLotNumber(), l.getExpirationDate(),
                            l.getQuantity(), l.getSalePrice(), l.getStatusName());
                }
                Lot suggested = controller.suggestFefoLot(productId);
                System.out.println("** Sugerencia FEFO: lote " + suggested.getLotNumber()
                        + " (id " + suggested.getId() + ") **");
                int lotId = ConsoleUtils.readInt("Id del lote a usar: ");
                BigDecimal qty = ConsoleUtils.readDecimal("Cantidad a vender: ");
                SaleDetail line = controller.buildLine(lotId, qty);
                if (line == null) {
                    System.out.println("Lote no encontrado.");
                } else {
                    cart.add(line);
                    System.out.printf("Linea agregada: %s x%s | Desc: $%s | IVA: $%s | Total: $%s%n",
                            line.getProductName(), line.getQuantitySold(),
                            line.getDiscountAmount(), line.getTaxAmount(), line.getLineTotal());
                    if (line.getDiscountAmount().compareTo(BigDecimal.ZERO) > 0) {
                        System.out.println("  (Promocion automatica aplicada por proximidad de vencimiento)");
                    }
                }
            }
            addMore = ConsoleUtils.readYesNo("Agregar otro producto?");
        }

        if (cart.isEmpty()) {
            System.out.println("Venta cancelada: no se agregaron productos.");
            ConsoleUtils.pause();
            return;
        }
        if (!ConsoleUtils.readYesNo("Confirmar la venta?")) {
            System.out.println("Venta cancelada por el usuario.");
            ConsoleUtils.pause();
            return;
        }
        String user = ConsoleUtils.readString("Usuario que registra: ");
        Sale sale = controller.confirmSale(customerId, cart, user);
        System.out.println("\nVenta confirmada: " + sale.getSaleNumber());
        System.out.printf("Subtotal: $%s | Descuento: $%s | IVA: $%s | TOTAL: $%s%n",
                sale.getSubtotal(), sale.getTotalDiscount(),
                sale.getTotalTax(), sale.getTotalAmount());
        System.out.println("Estado: En Preparacion (disponible para el panel de despacho).");

        if (ConsoleUtils.readYesNo("Generar factura electronica ahora (un clic)?")) {
            emitInvoice(sale.getId());
        }
        ConsoleUtils.pause();
    }

    private void listSales() {
        System.out.println("\n--- Ventas registradas ---");
        List<Sale> sales = controller.listSales();
        if (sales.isEmpty()) {
            System.out.println("No hay ventas registradas.");
        } else {
            for (Sale s : sales) {
                System.out.printf("  [%d] %s%n", s.getId(), s);
            }
        }
        ConsoleUtils.pause();
    }

    private void saleDetail() {
        System.out.println("\n--- Detalle de venta ---");
        int saleId = ConsoleUtils.readInt("Id de la venta: ");
        Sale sale = controller.findSaleWithDetails(saleId);
        if (sale == null) {
            System.out.println("Venta no encontrada.");
            ConsoleUtils.pause();
            return;
        }
        System.out.println("Venta: " + sale.getSaleNumber() + " | Cliente: " + sale.getCustomerName());
        for (SaleDetail d : sale.getDetails()) {
            System.out.printf("  %s | Lote %s | x%s | Unit: $%s | Desc: $%s | IVA: $%s | Total: $%s%n",
                    d.getProductName(), d.getLotNumber(), d.getQuantitySold(),
                    d.getUnitPrice(), d.getDiscountAmount(), d.getTaxAmount(), d.getLineTotal());
        }
        System.out.printf("TOTAL: $%s%n", sale.getTotalAmount());
        ConsoleUtils.pause();
    }

    private void generateInvoice() {
        System.out.println("\n--- Generar factura electronica ---");
        int saleId = ConsoleUtils.readInt("Id de la venta: ");
        emitInvoice(saleId);
        ConsoleUtils.pause();
    }

    private void emitInvoice(int saleId) {
        try {
            List<Path> files = controller.generateInvoice(saleId);
            System.out.println("Factura generada correctamente:");
            for (Path p : files) {
                System.out.println("  -> " + p.toAbsolutePath());
            }
        } catch (RuntimeException ex) {
            System.out.println("No se pudo generar la factura: " + ex.getMessage());
        }
    }
}
