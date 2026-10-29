package com.frescuratotal.erp.suppliers.model;

import java.math.BigDecimal;

/**
 * Product entity. Maps the Spanish table {@code productos}.
 *
 * <p>Column mapping: id_producto -> id, nombre -> name,
 * descripcion -> description, id_categoria -> categoryId,
 * unidad_medida -> unitOfMeasure, precio_base -> basePrice.</p>
 */
public class Product {

    private int id;
    private String name;
    private String description;
    private int categoryId;
    private String categoryName; // convenience field populated by joins
    private String unitOfMeasure;
    private BigDecimal basePrice;

    public Product() {
    }

    public Product(String name, String description, int categoryId,
                   String unitOfMeasure, BigDecimal basePrice) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.unitOfMeasure = unitOfMeasure;
        this.basePrice = basePrice;
    }

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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getUnitOfMeasure() {
        return unitOfMeasure;
    }

    public void setUnitOfMeasure(String unitOfMeasure) {
        this.unitOfMeasure = unitOfMeasure;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (%s) - $%s",
                id, name, unitOfMeasure, basePrice);
    }
}
