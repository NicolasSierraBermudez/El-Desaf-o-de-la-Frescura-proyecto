package com.frescuratotal.erp.logistics.model;

import java.math.BigDecimal;

/**
 * Vehicle entity. Maps the Spanish table {@code vehiculos}.
 *
 * <p>Column mapping: id_vehiculo -> id, placa -> plate, modelo -> model,
 * capacidad -> capacity, estado -> status ('Activo'/'Inactivo'/'Mantenimiento').</p>
 */
public class Vehicle {

    public static final String STATUS_ACTIVE = "Activo";
    public static final String STATUS_INACTIVE = "Inactivo";
    public static final String STATUS_MAINTENANCE = "Mantenimiento";

    private int id;
    private String plate;
    private String model;
    private BigDecimal capacity;
    private String status = STATUS_ACTIVE;

    public Vehicle() {
    }

    public Vehicle(String plate, String model, BigDecimal capacity, String status) {
        this.plate = plate;
        this.model = model;
        this.capacity = capacity;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlate() {
        return plate;
    }

    public void setPlate(String plate) {
        this.plate = plate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public BigDecimal getCapacity() {
        return capacity;
    }

    public void setCapacity(BigDecimal capacity) {
        this.capacity = capacity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s (Cap: %s) - %s",
                id, plate, model, capacity, status);
    }
}
