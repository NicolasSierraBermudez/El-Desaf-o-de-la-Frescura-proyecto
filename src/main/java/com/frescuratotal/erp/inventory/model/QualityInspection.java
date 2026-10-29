package com.frescuratotal.erp.inventory.model;

import java.time.LocalDateTime;

/**
 * Quality inspection entity. Maps the Spanish table {@code inspecciones_calidad}.
 *
 * <p>Column mapping: id_inspeccion -> id, id_lote -> lotId,
 * fecha_inspeccion -> inspectionDate, resultado -> result ("Aprobado"/"Rechazado"),
 * nombre_inspector -> inspectorName, motivo_rechazo -> rejectionReason,
 * notas -> notes.</p>
 */
public class QualityInspection {

    /** Allowed value for an approved inspection ({@code resultado}). */
    public static final String RESULT_APPROVED = "Aprobado";
    /** Allowed value for a rejected inspection ({@code resultado}). */
    public static final String RESULT_REJECTED = "Rechazado";

    private int id;
    private int lotId;
    private LocalDateTime inspectionDate;
    private String result;
    private String inspectorName;
    private String rejectionReason;
    private String notes;

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

    public LocalDateTime getInspectionDate() {
        return inspectionDate;
    }

    public void setInspectionDate(LocalDateTime inspectionDate) {
        this.inspectionDate = inspectionDate;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public String getInspectorName() {
        return inspectorName;
    }

    public void setInspectorName(String inspectorName) {
        this.inspectorName = inspectorName;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getLotNumber() {
        return lotNumber;
    }

    public void setLotNumber(String lotNumber) {
        this.lotNumber = lotNumber;
    }

    public boolean isApproved() {
        return RESULT_APPROVED.equalsIgnoreCase(result);
    }

    @Override
    public String toString() {
        return String.format("[%d] Lote %s - %s por %s",
                id, lotNumber != null ? lotNumber : lotId, result, inspectorName);
    }
}
