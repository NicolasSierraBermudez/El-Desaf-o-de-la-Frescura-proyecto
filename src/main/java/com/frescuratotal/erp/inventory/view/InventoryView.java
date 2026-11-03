package com.frescuratotal.erp.inventory.view;

import com.frescuratotal.erp.common.ConsoleUtils;
import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.inventory.controller.InventoryController;
import com.frescuratotal.erp.inventory.model.Lot;
import com.frescuratotal.erp.inventory.model.LotStatus;
import com.frescuratotal.erp.inventory.model.QualityInspection;
import com.frescuratotal.erp.inventory.model.QuarantineRecord;
import com.frescuratotal.erp.inventory.model.WasteRecord;
import com.frescuratotal.erp.sales.model.Customer;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Console view for the inventory &amp; quality module (the central module).
 * Presentation only; all business rules live in {@link InventoryController}.
 * User-facing text is Spanish.
 */
public class InventoryView {

    private final InventoryController controller = new InventoryController();

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== MODULO: INVENTARIO Y CALIDAD =====");
            System.out.println("1. Registrar recepcion de mercancia (loteo)");
            System.out.println("2. Listar todos los lotes");
            System.out.println("3. Listar lotes por estado");
            System.out.println("4. Ejecutar revision diaria (vencimientos)");
            System.out.println("5. Enviar lote a cuarentena");
            System.out.println("6. Registrar inspeccion de calidad");
            System.out.println("7. Registrar merma manual");
            System.out.println("8. Trazabilidad de un lote");
            System.out.println("9. Ver registros de cuarentena / inspecciones / mermas");
            System.out.println("10. Reporte de inventario (resumen con Stream API)");
            System.out.println("0. Volver al menu principal");
            int option = ConsoleUtils.readInt("Seleccione una opcion: ", 0, 10);
            try {
                switch (option) {
                    case 1: registerReception(); break;
                    case 2: listAllLots(); break;
                    case 3: listByStatus(); break;
                    case 4: dailyReview(); break;
                    case 5: quarantine(); break;
                    case 6: inspection(); break;
                    case 7: waste(); break;
                    case 8: traceability(); break;
                    case 9: registries(); break;
                    case 10: inventoryReport(); break;
                    case 0: back = true; break;
                    default: break;
                }
            } catch (DaoException ex) {
                com.frescuratotal.erp.common.AppLogger.error(
                        "Error de BD en modulo Inventario", ex);
                System.out.println("\n[ERROR DE BASE DE DATOS] " + ex.getMessage());
                System.out.println("Verifique que MySQL este activo y la BD creada.");
                ConsoleUtils.pause();
            }
        }
    }

    private void registerReception() {
        System.out.println("\n--- Recepcion de mercancia ---");
        int productId = ConsoleUtils.readInt("Id del producto: ");
        int supplierId = ConsoleUtils.readInt("Id del proveedor: ");
        BigDecimal quantity = ConsoleUtils.readDecimal("Cantidad recibida: ");
        LocalDate expiration = ConsoleUtils.readDate("Fecha de vencimiento");
        BigDecimal purchasePrice = ConsoleUtils.readDecimal("Precio de compra unitario: ");
        BigDecimal salePrice = ConsoleUtils.readDecimal("Precio de venta unitario: ");
        String aisle = ConsoleUtils.readString("Pasillo: ");
        String shelf = ConsoleUtils.readString("Estante: ");
        Lot lot = controller.registerReception(productId, supplierId, quantity,
                expiration, purchasePrice, salePrice, aisle, shelf);
        System.out.println("Lote creado: " + lot.getLotNumber()
                + " (estado inicial: Disponible).");
        ConsoleUtils.pause();
    }

    private void listAllLots() {
        System.out.println("\n--- Todos los lotes ---");
        printLots(controller.listAllLots());
        ConsoleUtils.pause();
    }

    private void listByStatus() {
        System.out.println("\n--- Lotes por estado ---");
        System.out.println("1. Disponible  2. Proximo a Vencer  3. Vencido  4. En Cuarentena");
        int st = ConsoleUtils.readInt("Estado: ", 1, 4);
        printLots(controller.listLotsByStatus(LotStatus.fromId(st)));
        ConsoleUtils.pause();
    }

    private void printLots(List<Lot> lots) {
        if (lots.isEmpty()) {
            System.out.println("No hay lotes para mostrar.");
        } else {
            for (Lot l : lots) {
                System.out.printf("  [%d] %s%n", l.getId(), l);
            }
        }
    }

    private void dailyReview() {
        System.out.println("\n--- Revision diaria ---");
        String summary = controller.runDailyReview();
        System.out.println(summary);
        ConsoleUtils.pause();
    }

    private void quarantine() {
        System.out.println("\n--- Enviar lote a cuarentena ---");
        int lotId = ConsoleUtils.readInt("Id del lote: ");
        String reason = ConsoleUtils.readRequiredString("Motivo: ");
        String user = ConsoleUtils.readString("Usuario responsable: ");
        controller.quarantineLot(lotId, reason, user);
        System.out.println("Lote enviado a cuarentena y bloqueado para venta.");
        ConsoleUtils.pause();
    }

    private void inspection() {
        System.out.println("\n--- Inspeccion de calidad ---");
        int lotId = ConsoleUtils.readInt("Id del lote: ");
        boolean approved = ConsoleUtils.readYesNo("Resultado APROBADO?");
        String inspector = ConsoleUtils.readRequiredString("Nombre del inspector: ");
        String rejectionReason = null;
        if (!approved) {
            rejectionReason = ConsoleUtils.readRequiredString("Motivo del rechazo: ");
        }
        String notes = ConsoleUtils.readString("Notas: ");
        String result = controller.registerInspection(lotId, approved, inspector,
                rejectionReason, notes);
        System.out.println(result);
        ConsoleUtils.pause();
    }

    private void waste() {
        System.out.println("\n--- Registrar merma ---");
        int lotId = ConsoleUtils.readInt("Id del lote: ");
        BigDecimal quantity = ConsoleUtils.readDecimal("Cantidad perdida: ");
        String reason = ConsoleUtils.readRequiredString("Motivo: ");
        String user = ConsoleUtils.readString("Usuario responsable: ");
        String result = controller.registerWaste(lotId, quantity, reason, user);
        System.out.println(result);
        ConsoleUtils.pause();
    }

    private void traceability() {
        System.out.println("\n--- Trazabilidad de un lote ---");
        int lotId = ConsoleUtils.readInt("Id del lote: ");
        Lot lot = controller.findLot(lotId);
        if (lot == null) {
            System.out.println("Lote no encontrado.");
            ConsoleUtils.pause();
            return;
        }
        System.out.println("Lote: " + lot.getLotNumber());
        System.out.println("Producto: " + lot.getProductName());
        System.out.println("Proveedor de origen: " + controller.supplierOfLot(lotId));
        System.out.println("Unidades restantes: " + controller.remainingUnits(lotId));
        System.out.println("Clientes que recibieron producto de este lote:");
        List<Customer> customers = controller.customersOfLot(lotId);
        if (customers.isEmpty()) {
            System.out.println("  (ninguno aun)");
        } else {
            for (Customer c : customers) {
                System.out.println("  " + c);
            }
        }
        ConsoleUtils.pause();
    }

    private void registries() {
        System.out.println("\n--- Registros de calidad ---");
        System.out.println(">> Cuarentenas:");
        List<QuarantineRecord> qs = controller.listQuarantines();
        if (qs.isEmpty()) {
            System.out.println("   (sin registros)");
        } else {
            for (QuarantineRecord q : qs) {
                System.out.printf("   Lote %d | %s | Resuelta: %s%n",
                        q.getLotId(), q.getReason(), q.isResolved() ? "Si" : "No");
            }
        }
        System.out.println(">> Inspecciones:");
        List<QualityInspection> is = controller.listInspections();
        if (is.isEmpty()) {
            System.out.println("   (sin registros)");
        } else {
            for (QualityInspection i : is) {
                System.out.printf("   Lote %d | %s | Inspector: %s%n",
                        i.getLotId(), i.getResult(), i.getInspectorName());
            }
        }
        System.out.println(">> Mermas:");
        List<WasteRecord> ws = controller.listWaste();
        if (ws.isEmpty()) {
            System.out.println("   (sin registros)");
        } else {
            for (WasteRecord w : ws) {
                System.out.printf("   Lote %d | Cantidad: %s | %s%n",
                        w.getLotId(), w.getLostQuantity(), w.getReason());
            }
        }
        ConsoleUtils.pause();
    }

    /**
     * Inventory summary report built entirely from Stream-based aggregations in the
     * controller (counts by status, near-expiry list, stock value and total waste).
     */
    private void inventoryReport() {
        System.out.println("\n--- Reporte de inventario (resumen) ---");
        System.out.println(">> Lotes por estado:");
        Map<LotStatus, Long> byStatus = controller.countLotsByStatus();
        if (byStatus.isEmpty()) {
            System.out.println("   (sin lotes registrados)");
        } else {
            // forEach with a lambda over the report map.
            byStatus.forEach((status, count) ->
                    System.out.printf("   %-20s : %d lote(s)%n",
                            status != null ? status.getDisplayName() : "Desconocido", count));
        }

        System.out.println(">> Lotes que vencen en los proximos 7 dias (orden FEFO):");
        List<Lot> nearExpiry = controller.lotsExpiringWithin(7);
        if (nearExpiry.isEmpty()) {
            System.out.println("   (ninguno)");
        } else {
            nearExpiry.forEach(l ->
                    System.out.printf("   [%d] %s | Vence: %s | Cant: %s%n",
                            l.getId(), l.getLotNumber(), l.getExpirationDate(), l.getQuantity()));
        }

        System.out.printf(">> Valor total del inventario (precio venta x cantidad): $%s%n",
                controller.totalStockValue());
        System.out.printf(">> Total de unidades en merma: %s%n", controller.totalWasteUnits());
        ConsoleUtils.pause();
    }
}
