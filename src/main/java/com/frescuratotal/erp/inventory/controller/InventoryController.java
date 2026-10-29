package com.frescuratotal.erp.inventory.controller;

import com.frescuratotal.erp.common.DaoFactory;
import com.frescuratotal.erp.common.NumberGenerator;
import com.frescuratotal.erp.inventory.model.InspectionDao;
import com.frescuratotal.erp.inventory.model.Lot;
import com.frescuratotal.erp.inventory.model.LotDao;
import com.frescuratotal.erp.inventory.model.LotStatus;
import com.frescuratotal.erp.inventory.model.QualityInspection;
import com.frescuratotal.erp.inventory.model.QuarantineDao;
import com.frescuratotal.erp.inventory.model.QuarantineRecord;
import com.frescuratotal.erp.inventory.model.WasteDao;
import com.frescuratotal.erp.inventory.model.WasteRecord;
import com.frescuratotal.erp.sales.model.Customer;
import com.frescuratotal.erp.sales.model.CustomerDao;
import com.frescuratotal.erp.sales.model.SaleDao;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for the inventory &amp; quality module (the central module).
 *
 * <p>Implements reception/lotification, dynamic lot states, the manual "daily
 * review" job, quarantine, quality inspections, waste and traceability queries.
 * All persistence goes through DAOs (DIP); this class only holds business rules.</p>
 */
public class InventoryController {

    private final LotDao lotDao = DaoFactory.lotDao();
    private final SaleDao saleDao = DaoFactory.saleDao();
    private final CustomerDao customerDao = DaoFactory.customerDao();
    private final QuarantineDao quarantineDao = new QuarantineDao();
    private final InspectionDao inspectionDao = new InspectionDao();
    private final WasteDao wasteDao = new WasteDao();

    /** Days before expiration at which a lot becomes "Proximo a Vencer". */
    public static final int NEAR_EXPIRY_DAYS = 7;

    /**
     * Registers an incoming goods reception, generating a unique lot number and
     * setting the initial status to Disponible.
     *
     * @return the created lot with its generated id and number
     */
    public Lot registerReception(int productId, int supplierId, BigDecimal quantity,
                                 LocalDate expirationDate, BigDecimal purchasePrice,
                                 BigDecimal salePrice, String aisle, String shelf) {
        Lot lot = new Lot();
        lot.setLotNumber(NumberGenerator.nextLotNumber());
        lot.setProductId(productId);
        lot.setSupplierId(supplierId);
        lot.setExpirationDate(expirationDate);
        lot.setInitialQuantity(quantity);
        lot.setQuantity(quantity);
        lot.setPurchasePrice(purchasePrice);
        lot.setSalePrice(salePrice);
        lot.setAisle(aisle);
        lot.setShelf(shelf);
        lot.setStatusId(LotStatus.AVAILABLE.getId());
        lotDao.create(lot);
        return lot;
    }

    public List<Lot> listAllLots() {
        return lotDao.findAll();
    }

    public List<Lot> listLotsByStatus(LotStatus status) {
        return lotDao.findByStatus(status.getId());
    }

    public Lot findLot(int lotId) {
        return lotDao.findById(lotId).orElse(null);
    }

    // ---------------- Traceability ----------------

    /** @return the supplier name of a lot, or null. */
    public String supplierOfLot(int lotId) {
        Lot l = lotDao.findById(lotId).orElse(null);
        return l != null ? l.getSupplierName() : null;
    }

    /** @return remaining units of a lot, or null if not found. */
    public BigDecimal remainingUnits(int lotId) {
        Lot l = lotDao.findById(lotId).orElse(null);
        return l != null ? l.getQuantity() : null;
    }

    /** @return customers that received products from a given lot. */
    public List<Customer> customersOfLot(int lotId) {
        List<Customer> customers = new ArrayList<>();
        for (Integer cid : saleDao.findCustomerIdsByLot(lotId)) {
            customerDao.findById(cid).ifPresent(customers::add);
        }
        return customers;
    }

    // ---------------- Daily review job ----------------

    /**
     * Runs the daily review: marks lots as "Proximo a Vencer" when they expire
     * within {@link #NEAR_EXPIRY_DAYS} days, and "Vencido" when already expired.
     * Lots in quarantine are left untouched. Expired lots become blocked for sale
     * (their status is no longer in the sellable set).
     *
     * @return a human-readable summary of the changes applied
     */
    public String runDailyReview() {
        int nearCount = 0;
        int expiredCount = 0;
        LocalDate today = LocalDate.now();
        for (Lot lot : lotDao.findAll()) {
            // Never auto-change lots under quarantine.
            if (lot.getStatusId() == LotStatus.QUARANTINE.getId()) {
                continue;
            }
            LocalDate exp = lot.getExpirationDate();
            if (exp == null) {
                continue;
            }
            long daysToExpire = ChronoUnit.DAYS.between(today, exp);
            if (daysToExpire < 0) {
                if (lot.getStatusId() != LotStatus.EXPIRED.getId()) {
                    lotDao.updateStatus(lot.getId(), LotStatus.EXPIRED.getId());
                    expiredCount++;
                }
            } else if (daysToExpire <= NEAR_EXPIRY_DAYS) {
                if (lot.getStatusId() == LotStatus.AVAILABLE.getId()) {
                    lotDao.updateStatus(lot.getId(), LotStatus.NEAR_EXPIRY.getId());
                    nearCount++;
                }
            }
        }
        return String.format(
                "Revision diaria completada. Lotes marcados Proximo a Vencer: %d | Vencidos: %d",
                nearCount, expiredCount);
    }

    // ---------------- Quarantine ----------------

    /** Puts a lot into quarantine (blocked for sale) and records the reason. */
    public void quarantineLot(int lotId, String reason, String user) {
        QuarantineRecord q = new QuarantineRecord();
        q.setLotId(lotId);
        q.setReason(reason);
        q.setUser(user);
        quarantineDao.create(q);
        lotDao.updateStatus(lotId, LotStatus.QUARANTINE.getId());
    }

    public List<QuarantineRecord> listQuarantines() {
        return quarantineDao.findAll();
    }

    // ---------------- Quality inspection ----------------

    /**
     * Registers a quality inspection for a lot.
     * <ul>
     *   <li>Approved: lot returns to Disponible and the open quarantine is resolved.</li>
     *   <li>Rejected: a waste record is created for the full remaining quantity and
     *       the open quarantine is resolved; the lot quantity is set to zero.</li>
     * </ul>
     *
     * @return a summary message
     */
    public String registerInspection(int lotId, boolean approved, String inspector,
                                     String rejectionReason, String notes) {
        Lot lot = lotDao.findById(lotId).orElse(null);
        if (lot == null) {
            return "Lote no encontrado.";
        }
        QualityInspection insp = new QualityInspection();
        insp.setLotId(lotId);
        insp.setResult(approved ? QualityInspection.RESULT_APPROVED
                : QualityInspection.RESULT_REJECTED);
        insp.setInspectorName(inspector);
        insp.setRejectionReason(approved ? null : rejectionReason);
        insp.setNotes(notes);
        int inspId = inspectionDao.create(insp);

        if (approved) {
            lotDao.updateStatus(lotId, LotStatus.AVAILABLE.getId());
            quarantineDao.resolveByLot(lotId, "Aprobado por inspeccion #" + inspId);
            return "Inspeccion APROBADA. El lote vuelve a estado Disponible.";
        } else {
            BigDecimal remaining = lot.getQuantity() != null ? lot.getQuantity() : BigDecimal.ZERO;
            WasteRecord waste = new WasteRecord();
            waste.setLotId(lotId);
            waste.setInspectionId(inspId);
            waste.setLostQuantity(remaining);
            waste.setReason("Rechazo de calidad: "
                    + (rejectionReason != null ? rejectionReason : "sin detalle"));
            waste.setRegisteredBy(inspector);
            wasteDao.create(waste);
            lotDao.updateQuantity(lotId, BigDecimal.ZERO);
            quarantineDao.resolveByLot(lotId, "Rechazado por inspeccion #" + inspId);
            lotDao.updateStatus(lotId, LotStatus.EXPIRED.getId());
            return "Inspeccion RECHAZADA. Se genero merma por " + remaining
                    + " unidades y se bloqueo el lote.";
        }
    }

    public List<QualityInspection> listInspections() {
        return inspectionDao.findAll();
    }

    // ---------------- Waste ----------------

    /**
     * Registers a manual waste entry and discounts the quantity from the lot.
     *
     * @return a summary message
     */
    public String registerWaste(int lotId, BigDecimal quantity, String reason, String user) {
        Lot lot = lotDao.findById(lotId).orElse(null);
        if (lot == null) {
            return "Lote no encontrado.";
        }
        BigDecimal current = lot.getQuantity() != null ? lot.getQuantity() : BigDecimal.ZERO;
        if (quantity.compareTo(current) > 0) {
            return "La cantidad de merma (" + quantity + ") supera las unidades disponibles ("
                    + current + ").";
        }
        WasteRecord waste = new WasteRecord();
        waste.setLotId(lotId);
        waste.setLostQuantity(quantity);
        waste.setReason(reason);
        waste.setRegisteredBy(user);
        wasteDao.create(waste);
        lotDao.updateQuantity(lotId, current.subtract(quantity));
        return "Merma registrada. Unidades restantes del lote: " + current.subtract(quantity);
    }

    public List<WasteRecord> listWaste() {
        return wasteDao.findAll();
    }
}
