package com.frescuratotal.erp.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Inventory lot entity. Maps the Spanish table {@code lotes_inventario}.
 *
 * <p>Column mapping: id_lote -> id, numero_lote -> lotNumber,
 * id_producto -> productId, id_proveedor -> supplierId,
 * fecha_ingreso -> entryDate, fecha_vencimiento -> expirationDate,
 * cantidad_inicial -> initialQuantity, cantidad -> quantity,
 * precio_compra -> purchasePrice, precio_venta -> salePrice,
 * pasillo -> aisle, estante -> shelf, id_estado -> statusId.</p>
 */
public class Lot {

    private int id;
    private String lotNumber;
    private int productId;
    private int supplierId;
    private LocalDateTime entryDate;
    private LocalDate expirationDate;
    private BigDecimal initialQuantity;
    private BigDecimal quantity;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private String aisle;
    private String shelf;
    private int statusId;

    // Convenience fields populated by joins (not persisted directly).
    private String productName;
    private String supplierName;
    private String statusName;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(int supplierId) {
        this.supplierId = supplierId;
    }

    public LocalDateTime getEntryDate() {
        return entryDate;
    }

    public void setEntryDate(LocalDateTime entryDate) {
        this.entryDate = entryDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public BigDecimal getInitialQuantity() {
        return initialQuantity;
    }

    public void setInitialQuantity(BigDecimal initialQuantity) {
        this.initialQuantity = initialQuantity;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(BigDecimal purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public BigDecimal getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(BigDecimal salePrice) {
        this.salePrice = salePrice;
    }

    public String getAisle() {
        return aisle;
    }

    public void setAisle(String aisle) {
        this.aisle = aisle;
    }

    public String getShelf() {
        return shelf;
    }

    public void setShelf(String shelf) {
        this.shelf = shelf;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    @Override
    public String toString() {
        return String.format("%s | %s | Vence: %s | Cant: %s | %s",
                lotNumber,
                productName != null ? productName : ("Producto " + productId),
                expirationDate, quantity,
                statusName != null ? statusName : ("Estado " + statusId));
    }
}
