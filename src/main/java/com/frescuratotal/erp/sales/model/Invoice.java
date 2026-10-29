package com.frescuratotal.erp.sales.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Invoice entity. Maps the Spanish table {@code facturas}.
 *
 * <p>Column mapping: id_factura -> id, id_venta -> saleId,
 * numero_factura -> invoiceNumber, cufe -> cufe, enlace_qr -> qrLink,
 * fecha_emision -> issueDate, subtotal -> subtotal,
 * total_descuento -> totalDiscount, monto_impuesto -> taxAmount,
 * monto_total -> totalAmount.</p>
 */
public class Invoice {

    private int id;
    private int saleId;
    private String invoiceNumber;
    private String cufe;
    private String qrLink;
    private LocalDateTime issueDate;
    private BigDecimal subtotal;
    private BigDecimal totalDiscount = BigDecimal.ZERO;
    private BigDecimal taxAmount;
    private BigDecimal totalAmount;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSaleId() {
        return saleId;
    }

    public void setSaleId(int saleId) {
        this.saleId = saleId;
    }

    public String getInvoiceNumber() {
        return invoiceNumber;
    }

    public void setInvoiceNumber(String invoiceNumber) {
        this.invoiceNumber = invoiceNumber;
    }

    public String getCufe() {
        return cufe;
    }

    public void setCufe(String cufe) {
        this.cufe = cufe;
    }

    public String getQrLink() {
        return qrLink;
    }

    public void setQrLink(String qrLink) {
        this.qrLink = qrLink;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDateTime issueDate) {
        this.issueDate = issueDate;
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

    public BigDecimal getTaxAmount() {
        return taxAmount;
    }

    public void setTaxAmount(BigDecimal taxAmount) {
        this.taxAmount = taxAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return String.format("%s | CUFE: %s | Total: $%s",
                invoiceNumber, cufe, totalAmount);
    }
}
