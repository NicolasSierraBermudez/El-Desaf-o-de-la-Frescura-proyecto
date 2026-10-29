package com.frescuratotal.erp.inventory.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Waste record entity. Maps the Spanish table {@code registro_mermas}.
 *
 * <p>Column mapping: id_registro -> id, id_lote -> lotId,
 * id_inspeccion -> inspectionId (nullable), cantidad_perdida -> lostQuantity,
 * motivo -> reason, fecha_registro -> registrationDate,
 * usuario_registro -> registeredBy.</p>
 */
public class WasteRecord {

    private int id;
    private int lotId;
    private Integer inspectionId; // nullable
    private BigDecimal lostQuantity;
    private String reason;
    private LocalDateTime registrationDate;
    private String registeredBy;

    private String lotNumber; // convenience field from joins

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLotId() {
        return lotId;
    }

    public void setLotId(int lotId) {
        this.lotId = lotId;
    }

    public Integer getInspectionId() {
        return inspectionId;
    }

    public void setInspectionId(Integer inspectionId) {
        this.inspectionId = inspectionId;
    }

    public BigDecimal getLostQuantity() {
        return lostQuantity;
    }

    public void setLostQuantity(BigDecimal lostQuantity) {
        this.lostQuantity = lostQuantity;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getRegisteredBy() {
        return registeredBy;
    }

    public void setRegisteredBy(String registeredBy) {
        this.registeredBy = registeredBy;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    @Override
    public String toString() {
        return String.format("[%d] Lote %s - %s u. (%s)",
                id, lotNumber != null ? lotNumber : lotId, lostQuantity, reason);
    }
}
