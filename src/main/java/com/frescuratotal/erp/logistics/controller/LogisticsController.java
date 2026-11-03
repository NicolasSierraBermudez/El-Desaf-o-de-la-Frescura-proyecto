package com.frescuratotal.erp.logistics.controller;

import com.frescuratotal.erp.common.AppLogger;
import com.frescuratotal.erp.common.DaoFactory;
import com.frescuratotal.erp.logistics.model.DeliveryAssignment;
import com.frescuratotal.erp.logistics.model.DeliveryAssignmentDao;
import com.frescuratotal.erp.logistics.model.DeliveryRoute;
import com.frescuratotal.erp.logistics.model.DeliveryRouteDao;
import com.frescuratotal.erp.logistics.model.DeliveryRouteDaoImpl;
import com.frescuratotal.erp.logistics.model.Driver;
import com.frescuratotal.erp.logistics.model.DriverDao;
import com.frescuratotal.erp.logistics.model.RouteSale;
import com.frescuratotal.erp.logistics.model.RouteSaleDao;
import com.frescuratotal.erp.logistics.model.Vehicle;
import com.frescuratotal.erp.logistics.model.VehicleDao;
import com.frescuratotal.erp.sales.model.Sale;
import com.frescuratotal.erp.sales.model.SaleDao;
import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for the logistics &amp; delivery module.
 *
 * <p>Implements the dispatch panel (sales ready to ship = status "En Preparacion"),
 * route creation with vehicle/driver assignment, linking sales to a route and the
 * status transitions of both the route and its sales (En Transito -&gt; Entregada).
 * All persistence is delegated to DAOs (DIP); this class only holds the workflow.</p>
 */
public class LogisticsController {

    private final SaleDao saleDao = DaoFactory.saleDao();
    private final DriverDao driverDao = DaoFactory.driverDao();
    private final VehicleDao vehicleDao = DaoFactory.vehicleDao();
    private final DeliveryRouteDao routeDao = DaoFactory.deliveryRouteDao();
    private final DeliveryAssignmentDao assignmentDao = new DeliveryAssignmentDao();
    private final RouteSaleDao routeSaleDao = new RouteSaleDao();

    // ---------------- Drivers ----------------

    public int registerDriver(Driver d) {
        return driverDao.create(d);
    }

    public List<Driver> listDrivers() {
        return driverDao.findAll();
    }

    public Driver findDriver(int id) {
        return driverDao.findById(id).orElse(null);
    }

    // ---------------- Vehicles ----------------

    public int registerVehicle(Vehicle v) {
        return vehicleDao.create(v);
    }

    public List<Vehicle> listVehicles() {
        return vehicleDao.findAll();
    }

    public Vehicle findVehicle(int id) {
        return vehicleDao.findById(id).orElse(null);
    }

    // ---------------- Dispatch panel ----------------

    /**
     * @return the sales that are ready to be dispatched (status "En Preparacion").
     *         These are confirmed sales waiting to be assigned to a delivery route.
     */
    public List<Sale> dispatchPanel() {
        return saleDao.findByStatus(Sale.STATUS_IN_PREPARATION);
    }

    public List<Sale> salesInTransit() {
        return saleDao.findByStatus(Sale.STATUS_IN_TRANSIT);
    }

    /**
     * Report: total monetary value of the sales currently waiting to be dispatched.
     * Uses a Stream pipeline ({@code map(Sale::getTotalAmount).reduce(...)}).
     */
    public BigDecimal pendingDispatchValue() {
        return dispatchPanel().stream()
                .map(Sale::getTotalAmount)
                .filter(amount -> amount != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // ---------------- Routes ----------------

    public List<DeliveryRoute> listRoutes() {
        return routeDao.findAll();
    }

    public DeliveryRoute findRoute(int id) {
        return routeDao.findById(id).orElse(null);
    }

    /**
     * Creates a planned route, assigns a vehicle and driver to it and links the
     * given sales as stops (each sale moves to status "En Transito").
     *
     * @return the created route with its generated id
     */
    public DeliveryRoute createRoute(String routeName, int vehicleId, int driverId,
                                     List<Sale> sales, String user, String notes) {
        DeliveryRoute route = new DeliveryRoute();
        route.setRouteName(routeName);
        route.setCreatedBy(user);
        route.setStatus(DeliveryRoute.STATUS_PLANNED);
        routeDao.create(route);

        DeliveryAssignment assignment = new DeliveryAssignment();
        assignment.setRouteId(route.getId());
        assignment.setVehicleId(vehicleId);
        assignment.setDriverId(driverId);
        assignment.setAssignedBy(user);
        assignment.setNotes(notes);
        assignmentDao.create(assignment);

        int order = 1;
        for (Sale sale : sales) {
            RouteSale rs = new RouteSale();
            rs.setRouteId(route.getId());
            rs.setSaleId(sale.getId());
            rs.setDeliveryOrder(order++);
            routeSaleDao.create(rs);
            // The sale is now loaded in the route and leaves the warehouse.
            saleDao.updateStatus(sale.getId(), Sale.STATUS_IN_TRANSIT);
        }
        AppLogger.info("Ruta creada: " + routeName + " (id=" + route.getId()
                + ") vehiculo=" + vehicleId + " conductor=" + driverId
                + " ventas=" + sales.size());
        return route;
    }

    public List<DeliveryAssignment> assignmentsOfRoute(int routeId) {
        return assignmentDao.findByRoute(routeId);
    }

    public List<RouteSale> salesOfRoute(int routeId) {
        return routeSaleDao.findByRoute(routeId);
    }

    /**
     * Dispatches a planned route: the route moves to "En Transito". Its sales are
     * already in transit when linked.
     */
    public boolean dispatchRoute(int routeId) {
        return ((DeliveryRouteDaoImpl) routeDao)
                .updateStatus(routeId, DeliveryRoute.STATUS_IN_TRANSIT);
    }

    /**
     * Completes a route: the route moves to "Completada" and every sale linked to
     * it is marked "Entregada".
     *
     * @return the number of sales marked as delivered
     */
    public int completeRoute(int routeId) {
        // Stream API: mark every linked sale as delivered and count the successes
        // via filter + count, replacing the manual accumulator loop.
        long delivered = routeSaleDao.findByRoute(routeId).stream()
                .map(RouteSale::getSaleId)
                .filter(saleId -> saleDao.updateStatus(saleId, Sale.STATUS_DELIVERED))
                .count();
        ((DeliveryRouteDaoImpl) routeDao)
                .updateStatus(routeId, DeliveryRoute.STATUS_COMPLETED);
        AppLogger.info("Ruta " + routeId + " completada. Ventas entregadas=" + delivered);
        return (int) delivered;
    }

    public boolean cancelRoute(int routeId) {
        return ((DeliveryRouteDaoImpl) routeDao)
                .updateStatus(routeId, DeliveryRoute.STATUS_CANCELLED);
    }
}
