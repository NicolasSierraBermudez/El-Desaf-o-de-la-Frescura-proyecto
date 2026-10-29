package com.frescuratotal.erp.inventory.model;

import java.time.LocalDateTime;

/**
 * Quarantine record entity. Maps the Spanish table {@code registro_cuarentena}.
 *
 * <p>Column mapping: id_registro -> id, id_lote -> lotId, motivo -> reason,
 * usuario -> user, fecha_registro -> registrationDate, resuelta -> resolved,
 * fecha_resolucion -> resolutionDate, notas_resolucion -> resolutionNotes.</p>
 */
public class QuarantineRecord {

    private int id;
    private int lotId;
    private String reason;
    private String user;
    private LocalDateTime registrationDate;
    private boolean resolved;
    private LocalDateTime resolutionDate;
    private String resolutionNotes;

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

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;
    }

    public LocalDateTime getResolutionDate() {
        return resolutionDate;
    }

    public void setResolutionDate(LocalDateTime resolutionDate) {
        this.resolutionDate = resolutionDate;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    @Override
    public String toString() {
        return String.format("[%d] Lote %s - %s (%s)",
                id, lotNumber != null ? lotNumber : lotId, reason,
                resolved ? "Resuelta" : "Pendiente");
    }
}
