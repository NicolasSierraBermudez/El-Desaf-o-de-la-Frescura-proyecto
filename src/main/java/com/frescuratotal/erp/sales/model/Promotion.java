package com.frescuratotal.erp.sales.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Promotion entity. Maps the Spanish table {@code promociones}.
 *
 * <p>Column mapping: id_promocion -> id, nombre -> name,
 * descripcion -> description, tasa_descuento -> discountRate (e.g. 0.50 = 50%),
 * aplica_estado_lote -> appliesLotStatusId (nullable),
 * aplica_categoria_producto -> appliesCategoryId (nullable), activa -> active,
 * fecha_inicio -> startDate, fecha_fin -> endDate.</p>
 */
public class Promotion {

    private int id;
    private String name;
    private String description;
    private BigDecimal discountRate;
    private Integer appliesLotStatusId;
    private Integer appliesCategoryId;
    private boolean active = true;
    private LocalDate startDate;
    private LocalDate endDate;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public Integer getAppliesLotStatusId() {
        return appliesLotStatusId;
    }

    public void setAppliesLotStatusId(Integer appliesLotStatusId) {
        this.appliesLotStatusId = appliesLotStatusId;
    }

    public Integer getAppliesCategoryId() {
        return appliesCategoryId;
    }

    public void setAppliesCategoryId(Integer appliesCategoryId) {
        this.appliesCategoryId = appliesCategoryId;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s - %s%%",
                id, name,
                discountRate != null
                        ? discountRate.multiply(BigDecimal.valueOf(100)) : "0");
    }
}
