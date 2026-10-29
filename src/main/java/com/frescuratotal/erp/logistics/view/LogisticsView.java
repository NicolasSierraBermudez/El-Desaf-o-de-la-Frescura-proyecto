package com.frescuratotal.erp.logistics.view;

import com.frescuratotal.erp.common.ConsoleUtils;
import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.logistics.controller.LogisticsController;
import com.frescuratotal.erp.logistics.model.DeliveryAssignment;
import com.frescuratotal.erp.logistics.model.DeliveryRoute;
import com.frescuratotal.erp.logistics.model.Driver;
import com.frescuratotal.erp.logistics.model.RouteSale;
import com.frescuratotal.erp.logistics.model.Vehicle;
import com.frescuratotal.erp.sales.model.Sale;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Console view for the logistics &amp; delivery module. Presentation only; the
 * dispatch workflow lives in {@link LogisticsController}. Text is in Spanish.
 */
public class LogisticsView {

    private final LogisticsController controller = new LogisticsController();

    public void show() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== MODULO: LOGISTICA Y ENTREGAS =====");
            System.out.println("1. Registrar conductor");
            System.out.println("2. Listar conductores");
            System.out.println("3. Registrar vehiculo");
            System.out.println("4. Listar vehiculos");
            System.out.println("5. Panel de despacho (ventas listas para enviar)");
            System.out.println("6. Crear ruta y asignar (vehiculo + conductor + ventas)");
            System.out.println("7. Listar rutas");
            System.out.println("8. Ver detalle de una ruta");
            System.out.println("9. Despachar / Completar / Cancelar ruta");
            System.out.println("0. Volver al menu principal");
            int option = ConsoleUtils.readInt("Seleccione una opcion: ", 0, 9);
            try {
                switch (option) {
                    case 1: registerDriver(); break;
                    case 2: listDrivers(); break;
                    case 3: registerVehicle(); break;
                    case 4: listVehicles(); break;
                    case 5: dispatchPanel(); break;
                    case 6: createRoute(); break;
                    case 7: listRoutes(); break;
                    case 8: routeDetail(); break;
                    case 9: routeTransitions(); break;
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

    private void registerDriver() {
        System.out.println("\n--- Registro de conductor ---");
        String name = ConsoleUtils.readRequiredString("Nombre: ");
        String license = ConsoleUtils.readRequiredString("Numero de licencia: ");
        String phone = ConsoleUtils.readString("Telefono: ");
        String email = ConsoleUtils.readString("Email: ");
        Driver d = new Driver(name, license, phone, email, true);
        int id = controller.registerDriver(d);
        System.out.println("Conductor registrado con id " + id + ".");
        ConsoleUtils.pause();
    }

    private void listDrivers() {
        System.out.println("\n--- Conductores ---");
        List<Driver> list = controller.listDrivers();
        if (list.isEmpty()) {
            System.out.println("No hay conductores registrados.");
        } else {
            for (Driver d : list) {
                System.out.println("  " + d);
            }
        }
        ConsoleUtils.pause();
    }

    private void registerVehicle() {
        System.out.println("\n--- Registro de vehiculo ---");
        String plate = ConsoleUtils.readRequiredString("Placa: ");
        String model = ConsoleUtils.readString("Modelo: ");
        BigDecimal capacity = ConsoleUtils.readDecimal("Capacidad (kg): ");
        Vehicle v = new Vehicle(plate, model, capacity, Vehicle.STATUS_ACTIVE);
        int id = controller.registerVehicle(v);
        System.out.println("Vehiculo registrado con id " + id + ".");
        ConsoleUtils.pause();
    }

    private void listVehicles() {
        System.out.println("\n--- Vehiculos ---");
        List<Vehicle> list = controller.listVehicles();
        if (list.isEmpty()) {
            System.out.println("No hay vehiculos registrados.");
        } else {
            for (Vehicle v : list) {
                System.out.println("  " + v);
            }
        }
        ConsoleUtils.pause();
    }

    private void dispatchPanel() {
        System.out.println("\n--- Panel de despacho (ventas En Preparacion) ---");
        List<Sale> sales = controller.dispatchPanel();
        if (sales.isEmpty()) {
            System.out.println("No hay ventas listas para despachar.");
        } else {
            for (Sale s : sales) {
                System.out.printf("  [%d] %s%n", s.getId(), s);
            }
        }
        ConsoleUtils.pause();
    }

    private void createRoute() {
        System.out.println("\n--- Crear ruta de entrega ---");
        List<Sale> pending = controller.dispatchPanel();
        if (pending.isEmpty()) {
            System.out.println("No hay ventas En Preparacion para asignar a una ruta.");
            ConsoleUtils.pause();
            return;
        }
        System.out.println("Ventas disponibles para la ruta:");
        for (Sale s : pending) {
            System.out.printf("  [%d] %s%n", s.getId(), s);
        }
        System.out.println("Conductores:");
        for (Driver d : controller.listDrivers()) {
            System.out.println("  " + d);
        }
        System.out.println("Vehiculos:");
        for (Vehicle v : controller.listVehicles()) {
            System.out.println("  " + v);
        }

        String routeName = ConsoleUtils.readRequiredString("Nombre de la ruta: ");
        int vehicleId = ConsoleUtils.readInt("Id del vehiculo: ");
        int driverId = ConsoleUtils.readInt("Id del conductor: ");
        String notes = ConsoleUtils.readString("Notas de la asignacion: ");
        String user = ConsoleUtils.readString("Usuario que crea la ruta: ");

        List<Sale> selected = new ArrayList<>();
        boolean addMore = true;
        while (addMore) {
            int saleId = ConsoleUtils.readInt("Id de la venta a incluir: ");
            Sale chosen = null;
            for (Sale s : pending) {
                if (s.getId() == saleId) {
                    chosen = s;
                    break;
                }
            }
            if (chosen == null) {
                System.out.println("Esa venta no esta en el panel de despacho.");
            } else if (selected.contains(chosen)) {
                System.out.println("La venta ya fue agregada.");
            } else {
                selected.add(chosen);
                System.out.println("Venta agregada a la ruta.");
            }
            addMore = ConsoleUtils.readYesNo("Agregar otra venta?");
        }
        if (selected.isEmpty()) {
            System.out.println("Ruta cancelada: no se selecciono ninguna venta.");
            ConsoleUtils.pause();
            return;
        }
        DeliveryRoute route = controller.createRoute(routeName, vehicleId, driverId,
                selected, user, notes);
        System.out.println("Ruta creada con id " + route.getId()
                + ". Las ventas pasaron a estado En Transito.");
        ConsoleUtils.pause();
    }

    private void listRoutes() {
        System.out.println("\n--- Rutas de entrega ---");
        List<DeliveryRoute> routes = controller.listRoutes();
        if (routes.isEmpty()) {
            System.out.println("No hay rutas registradas.");
        } else {
            for (DeliveryRoute r : routes) {
                System.out.printf("  [%d] %s%n", r.getId(), r);
            }
        }
        ConsoleUtils.pause();
    }

    private void routeDetail() {
        System.out.println("\n--- Detalle de ruta ---");
        int routeId = ConsoleUtils.readInt("Id de la ruta: ");
        DeliveryRoute route = controller.findRoute(routeId);
        if (route == null) {
            System.out.println("Ruta no encontrada.");
            ConsoleUtils.pause();
            return;
        }
        System.out.println("Ruta: " + route.getRouteName() + " | Estado: " + route.getStatus());
        System.out.println("Asignaciones:");
        for (DeliveryAssignment a : controller.assignmentsOfRoute(routeId)) {
            System.out.printf("  Vehiculo %s | Conductor %s%n",
                    a.getVehiclePlate(), a.getDriverName());
        }
        System.out.println("Ventas en la ruta:");
        for (RouteSale rs : controller.salesOfRoute(routeId)) {
            System.out.printf("  Orden %s | Venta %s%n",
                    rs.getDeliveryOrder(), rs.getSaleNumber());
        }
        ConsoleUtils.pause();
    }

    private void routeTransitions() {
        System.out.println("\n--- Transiciones de estado de ruta ---");
        int routeId = ConsoleUtils.readInt("Id de la ruta: ");
        System.out.println("1. Despachar (En Transito)");
        System.out.println("2. Completar (Completada -> ventas Entregadas)");
        System.out.println("3. Cancelar (Cancelada)");
        int op = ConsoleUtils.readInt("Accion: ", 1, 3);
        switch (op) {
            case 1:
                controller.dispatchRoute(routeId);
                System.out.println("Ruta despachada (En Transito).");
                break;
            case 2:
                int delivered = controller.completeRoute(routeId);
                System.out.println("Ruta completada. Ventas marcadas como Entregadas: " + delivered);
                break;
            case 3:
                controller.cancelRoute(routeId);
                System.out.println("Ruta cancelada.");
                break;
            default:
                break;
        }
        ConsoleUtils.pause();
    }
}
