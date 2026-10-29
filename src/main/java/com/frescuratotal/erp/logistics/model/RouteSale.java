package com.frescuratotal.erp.logistics.model;

import java.time.LocalDateTime;

/**
 * Route-sale link entity. Maps the Spanish table {@code ventas_ruta}.
 *
 * <p>Column mapping: id -> id, id_ruta -> routeId, id_venta -> saleId,
 * orden_entrega -> deliveryOrder, direccion_destino -> destinationAddress,
 * fecha_entrega -> deliveryDate, recibido_por -> receivedBy,
 * observaciones -> observations.</p>
 */
public class RouteSale {

    private int id;
    private int routeId;
    private int saleId;
    private Integer deliveryOrder;
    private String destinationAddress;
    private LocalDateTime deliveryDate;
    private String receivedBy;
    private String observations;

    // Convenience field from joins.
    private String saleNumber;

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

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public Integer getDeliveryOrder() {
        return deliveryOrder;
    }

    public void setDeliveryOrder(Integer deliveryOrder) {
        this.deliveryOrder = deliveryOrder;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getReceivedBy() {
        return receivedBy;
    }

    public void setReceivedBy(String receivedBy) {
        this.receivedBy = receivedBy;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }
}
