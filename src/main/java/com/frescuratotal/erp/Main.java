package com.frescuratotal.erp;

import com.frescuratotal.erp.common.ConsoleUtils;
import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.common.DatabaseConfig;
import com.frescuratotal.erp.inventory.view.InventoryView;
import com.frescuratotal.erp.logistics.view.LogisticsView;
import com.frescuratotal.erp.sales.view.SalesView;
import com.frescuratotal.erp.suppliers.view.SupplierView;

/**
 * Application entry point for the "ERP Frescura Total" console application.
 *
 * <p>Displays the main menu (in Spanish) and dispatches to the four business
 * modules. Each module view already handles {@link DaoException} per operation,
 * so a missing/unavailable MySQL server shows a clear message instead of crashing
 * the whole application.</p>
 */
public final class Main {

    private Main() {
    }

    public static void main(String[] args) {
        printBanner();
        boolean exit = false;
        while (!exit) {
            System.out.println("\n==================================================");
            System.out.println("        ERP FRESCURA TOTAL - MENU PRINCIPAL");
            System.out.println("==================================================");
            System.out.println("1. Proveedores y Compras");
            System.out.println("2. Inventario y Calidad");
            System.out.println("3. Ventas y Facturacion");
            System.out.println("4. Logistica y Entregas");
            System.out.println("0. Salir");
            int option = ConsoleUtils.readInt("Seleccione un modulo: ", 0, 4);
            try {
                switch (option) {
                    case 1: new SupplierView().show(); break;
                    case 2: new InventoryView().show(); break;
                    case 3: new SalesView().show(); break;
                    case 4: new LogisticsView().show(); break;
                    case 0: exit = true; break;
                    default: break;
                }
            } catch (DaoException ex) {
                // Safety net in case a view does not catch a persistence error.
                System.out.println("\n[ERROR DE BASE DE DATOS] " + ex.getMessage());
                System.out.println("No se pudo conectar a MySQL. Revise que el servidor este");
                System.out.println("activo y que la base de datos exista. Credenciales en:");
                System.out.println("  com.frescuratotal.erp.common.DatabaseConfig");
                System.out.println("  URL actual: " + DatabaseConfig.getJdbcUrl());
                ConsoleUtils.pause();
            } catch (RuntimeException ex) {
                System.out.println("\n[ERROR INESPERADO] " + ex.getMessage());
                ConsoleUtils.pause();
            }
        }
        System.out.println("\nGracias por usar ERP Frescura Total. Hasta pronto.");
    }

    private static void printBanner() {
        System.out.println("##################################################");
        System.out.println("#   ERP para la Bodega de Alimentos               #");
        System.out.println("#            \"FRESCURA TOTAL\"                      #");
        System.out.println("#   Gestion de inventario perecedero y trazabilidad #");
        System.out.println("##################################################");
    }
}
