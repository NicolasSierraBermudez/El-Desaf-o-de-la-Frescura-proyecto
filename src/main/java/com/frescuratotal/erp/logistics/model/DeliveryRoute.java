package com.frescuratotal.erp.logistics.model;

import java.time.LocalDateTime;

/**
 * Delivery route entity. Maps the Spanish table {@code rutas_entrega}.
 *
 * <p>Column mapping: id_ruta -> id, nombre_ruta -> routeName,
 * fecha_creacion -> creationDate, usuario_creacion -> createdBy,
 * hora_salida_estimada -> estimatedDeparture,
 * hora_llegada_estimada -> estimatedArrival,
 * hora_salida_real -> actualDeparture, hora_llegada_real -> actualArrival,
 * estado -> status ('Planificada'/'En Transito'/'Completada'/'Cancelada').</p>
 */
public class DeliveryRoute {

    public static final String STATUS_PLANNED = "Planificada";
    public static final String STATUS_IN_TRANSIT = "En Transito";
    public static final String STATUS_COMPLETED = "Completada";
    public static final String STATUS_CANCELLED = "Cancelada";

    private int id;
    private String routeName;
    private LocalDateTime creationDate;
    private String createdBy;
    private LocalDateTime estimatedDeparture;
    private LocalDateTime estimatedArrival;
    private LocalDateTime actualDeparture;
    private LocalDateTime actualArrival;
    private String status = STATUS_PLANNED;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public LocalDateTime getEstimatedDeparture() {
        return estimatedDeparture;
    }

    public void setEstimatedDeparture(LocalDateTime estimatedDeparture) {
        this.estimatedDeparture = estimatedDeparture;
    }

    public LocalDateTime getEstimatedArrival() {
        return estimatedArrival;
    }

    public void setEstimatedArrival(LocalDateTime estimatedArrival) {
        this.estimatedArrival = estimatedArrival;
    }

    public LocalDateTime getActualDeparture() {
        return actualDeparture;
    }

    public void setActualDeparture(LocalDateTime actualDeparture) {
        this.actualDeparture = actualDeparture;
    }

    public LocalDateTime getActualArrival() {
        return actualArrival;
    }

    public void setActualArrival(LocalDateTime actualArrival) {
        this.actualArrival = actualArrival;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s", id, routeName, status);
    }
}
