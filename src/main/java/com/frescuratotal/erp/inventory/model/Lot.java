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

    /** Unique lot identifier (primary key). */
    private int id;
    /** Human-readable lot code assigned on reception. */
    private String lotNumber;
    /** Identifier of the product this lot belongs to. */
    private int productId;
    /** Identifier of the supplier that delivered the lot. */
    private int supplierId;
    /** Date and time when the lot entered the warehouse. */
    private LocalDateTime entryDate;
    /** Date on which the lot expires. */
    private LocalDate expirationDate;
    /** Quantity received when the lot was registered. */
    private BigDecimal initialQuantity;
    /** Quantity currently available in stock. */
    private BigDecimal quantity;
    /** Unit price paid to the supplier. */
    private BigDecimal purchasePrice;
    /** Unit price charged to customers. */
    private BigDecimal salePrice;
    /** Warehouse aisle where the lot is stored. */
    private String aisle;
    /** Shelf within the aisle where the lot is stored. */
    private String shelf;
    /** Identifier of the current lot status. */
    private int statusId;

    // Convenience fields populated by joins (not persisted directly).
    /** Product name, populated by joins. */
    private String productName;
    /** Supplier name, populated by joins. */
    private String supplierName;
    /** Status name, populated by joins. */
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
