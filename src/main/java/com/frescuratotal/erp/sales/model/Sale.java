package com.frescuratotal.erp.sales.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Sale (order) entity. Maps the Spanish table {@code ventas}.
 *
 * <p>Column mapping: id_venta -> id, numero_venta -> saleNumber,
 * id_cliente -> customerId, fecha_venta -> saleDate, subtotal -> subtotal,
 * total_descuento -> totalDiscount, total_impuesto -> totalTax,
 * monto_total -> totalAmount, id_estado -> statusId,
 * usuario_registro -> registeredBy.</p>
 *
 * <p>The sale status catalog ({@code estados_venta}) ids are deterministic from
 * the seed data: 1=Registrada, 2=En Preparacion, 3=En Transito, 4=Entregada,
 * 5=Cancelada.</p>
 */
public class Sale {

    public static final int STATUS_REGISTERED = 1;
    public static final int STATUS_IN_PREPARATION = 2;
    public static final int STATUS_IN_TRANSIT = 3;
    public static final int STATUS_DELIVERED = 4;
    public static final int STATUS_CANCELLED = 5;

    private int id;
    private String saleNumber;
    private int customerId;
    private LocalDateTime saleDate;
    private BigDecimal subtotal = BigDecimal.ZERO;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private BigDecimal totalTax = BigDecimal.ZERO;
    private BigDecimal totalAmount = BigDecimal.ZERO;
    private int statusId;
    private String registeredBy;

    // Convenience fields.
    private String customerName;
    private String statusName;
    private final List<SaleDetail> details = new ArrayList<>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSaleNumber() {
        return saleNumber;
    }

    public void setSaleNumber(String saleNumber) {
        this.saleNumber = saleNumber;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }

    public BigDecimal getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(BigDecimal totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public BigDecimal getTotalTax() {
        return totalTax;
    }

    public void setTotalTax(BigDecimal totalTax) {
        this.totalTax = totalTax;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public List<SaleDetail> getDetails() {
        return details;
    }

    @Override
    public String toString() {
        return String.format("%s | Cliente: %s | Total: $%s | %s",
                saleNumber,
                customerName != null ? customerName : ("Cliente " + customerId),
                totalAmount,
                statusName != null ? statusName : ("Estado " + statusId));
    }
}
