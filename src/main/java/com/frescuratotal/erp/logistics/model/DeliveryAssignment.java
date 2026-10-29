package com.frescuratotal.erp.logistics.model;

import java.time.LocalDateTime;

/**
 * Delivery assignment entity. Maps the Spanish table {@code asignaciones_entrega}.
 *
 * <p>Column mapping: id_asignacion -> id, id_ruta -> routeId,
 * id_vehiculo -> vehicleId, id_conductor -> driverId,
 * fecha_asignacion -> assignmentDate, usuario_asignacion -> assignedBy,
 * notas -> notes.</p>
 */
public class DeliveryAssignment {

    private int id;
    private int routeId;
    private int vehicleId;
    private int driverId;
    private LocalDateTime assignmentDate;
    private String assignedBy;
    private String notes;

    // Convenience fields from joins.
    private String routeName;
    private String vehiclePlate;
    private String driverName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRouteId() {
        return routeId;
    }

    public void setRouteId(int routeId) {
        this.routeId = routeId;
    }

    public int getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(int vehicleId) {
        this.vehicleId = vehicleId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public LocalDateTime getAssignmentDate() {
        return assignmentDate;
    }

    public void setAssignmentDate(LocalDateTime assignmentDate) {
        this.assignmentDate = assignmentDate;
    }

    public String getAssignedBy() {
        return assignedBy;
    }

    public void setAssignedBy(String assignedBy) {
        this.assignedBy = assignedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public String getVehiclePlate() {
        return vehiclePlate;
    }

    public void setVehiclePlate(String vehiclePlate) {
        this.vehiclePlate = vehiclePlate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }
}
