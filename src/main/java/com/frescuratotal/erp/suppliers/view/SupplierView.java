package com.frescuratotal.erp.suppliers.view;

import com.frescuratotal.erp.common.ConsoleUtils;
import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.suppliers.controller.SupplierController;
import com.frescuratotal.erp.suppliers.model.Category;
import com.frescuratotal.erp.suppliers.model.Product;
import com.frescuratotal.erp.suppliers.model.Supplier;
import com.frescuratotal.erp.inventory.model.Lot;
import java.math.BigDecimal;
import java.util.List;

/**
 * Console view for the suppliers &amp; purchases module (MVC: presentation layer).
 * All user-facing text is in Spanish; the view only handles I/O and delegates
 * every operation to {@link SupplierController}.
 */
public class SupplierView {

    private final SupplierController controller = new SupplierController();

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== MODULO: PROVEEDORES Y COMPRAS =====");
            System.out.println("1. Registrar proveedor");
            System.out.println("2. Listar proveedores");
            System.out.println("3. Registrar producto");
            System.out.println("4. Listar productos");
            System.out.println("5. Vincular producto a proveedor (catalogo)");
            System.out.println("6. Ver catalogo de un proveedor");
            System.out.println("7. Historial de compras (lotes) de un proveedor");
            System.out.println("0. Volver al menu principal");
            int option = ConsoleUtils.readInt("Seleccione una opcion: ", 0, 7);
            try {
                switch (option) {
                    case 1: registerSupplier(); break;
                    case 2: listSuppliers(); break;
                    case 3: registerProduct(); break;
                    case 4: listProducts(); break;
                    case 5: linkProduct(); break;
                    case 6: supplierCatalog(); break;
                    case 7: purchaseHistory(); break;
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

    private void registerSupplier() {
        System.out.println("\n--- Registro de proveedor ---");
        String name = ConsoleUtils.readRequiredString("Nombre/razon social: ");
        String nit = ConsoleUtils.readRequiredString("NIT: ");
        String contact = ConsoleUtils.readString("Persona de contacto: ");
        String email = ConsoleUtils.readString("Email: ");
        String phone = ConsoleUtils.readString("Telefono: ");
        String address = ConsoleUtils.readString("Direccion: ");
        Supplier s = new Supplier(name, nit, contact, email, phone, address, true);
        int id = controller.registerSupplier(s);
        System.out.println("Proveedor registrado con id " + id + ".");
        ConsoleUtils.pause();
    }

    private void listSuppliers() {
        System.out.println("\n--- Proveedores registrados ---");
        List<Supplier> list = controller.listSuppliers();
        if (list.isEmpty()) {
            System.out.println("No hay proveedores registrados.");
        } else {
            for (Supplier s : list) {
                System.out.println("  " + s);
            }
        }
        ConsoleUtils.pause();
    }

    private void registerProduct() {
        System.out.println("\n--- Registro de producto ---");
        List<Category> categories = controller.listCategories();
        System.out.println("Categorias disponibles:");
        for (Category c : categories) {
            System.out.println("  " + c);
        }
        int categoryId = ConsoleUtils.readInt("Id de categoria: ");
        String name = ConsoleUtils.readRequiredString("Nombre del producto: ");
        String description = ConsoleUtils.readString("Descripcion: ");
        String unit = ConsoleUtils.readRequiredString("Unidad de medida (ej: kg, unidad): ");
        BigDecimal basePrice = ConsoleUtils.readDecimal("Precio base: ");
        Product p = new Product(name, description, categoryId, unit, basePrice);
        int id = controller.registerProduct(p);
        System.out.println("Producto registrado con id " + id + ".");
        ConsoleUtils.pause();
    }

    private void listProducts() {
        System.out.println("\n--- Productos registrados ---");
        List<Product> list = controller.listProducts();
        if (list.isEmpty()) {
            System.out.println("No hay productos registrados.");
        } else {
            for (Product p : list) {
                System.out.printf("  [%d] %s | Categoria: %s | %s | $%s%n",
                        p.getId(), p.getName(),
                        p.getCategoryName() != null ? p.getCategoryName() : p.getCategoryId(),
                        p.getUnitOfMeasure(), p.getBasePrice());
            }
        }
        ConsoleUtils.pause();
    }

    private void linkProduct() {
        System.out.println("\n--- Vincular producto a proveedor ---");
        int supplierId = ConsoleUtils.readInt("Id del proveedor: ");
        int productId = ConsoleUtils.readInt("Id del producto: ");
        controller.linkProductToSupplier(supplierId, productId);
        System.out.println("Vinculo creado en el catalogo del proveedor.");
        ConsoleUtils.pause();
    }

    private void supplierCatalog() {
        System.out.println("\n--- Catalogo de un proveedor ---");
        int supplierId = ConsoleUtils.readInt("Id del proveedor: ");
        List<Product> products = controller.productsOfSupplier(supplierId);
        if (products.isEmpty()) {
            System.out.println("El proveedor no tiene productos vinculados.");
        } else {
            for (Product p : products) {
                System.out.println("  " + p);
            }
        }
        ConsoleUtils.pause();
    }

    private void purchaseHistory() {
        System.out.println("\n--- Historial de compras (lotes recibidos) ---");
        int supplierId = ConsoleUtils.readInt("Id del proveedor: ");
        List<Lot> lots = controller.purchaseHistory(supplierId);
        if (lots.isEmpty()) {
            System.out.println("No hay lotes registrados para este proveedor.");
        } else {
            for (Lot l : lots) {
                System.out.println("  " + l);
            }
        }
        ConsoleUtils.pause();
    }
}
