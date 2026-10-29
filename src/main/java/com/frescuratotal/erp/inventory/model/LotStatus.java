package com.frescuratotal.erp.inventory.model;

/**
 * Lot status catalog. Maps the Spanish table {@code estados_lote}.
 *
 * <p>The seed data in {@code docs/database.sql} inserts these four rows in this
 * exact order, so the auto-increment ids are deterministic. The enum keeps the
 * DB id alongside the Spanish display name used in the UI and reports.</p>
 */
public enum LotStatus {

    AVAILABLE(1, "Disponible"),
    NEAR_EXPIRY(2, "Proximo a Vencer"),
    EXPIRED(3, "Vencido"),
    QUARANTINE(4, "En Cuarentena");

    private final int id;
    private final String displayName;

    LotStatus(int id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public int getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Resolves the enum constant from its database id.
     *
     * @param id the {@code id_estado} value
     * @return the matching status, or {@code null} if unknown
     */
    public static LotStatus fromId(int id) {
        for (LotStatus s : values()) {
            if (s.id == id) {
                return s;
            }
        }
        return null;
    }

    /**
     * Resolves the enum constant from its Spanish display name.
     *
     * @param name the {@code nombre} value
     * @return the matching status, or {@code null} if unknown
     */
    public static LotStatus fromName(String name) {
        if (name == null) {
            return null;
        }
        for (LotStatus s : values()) {
            if (s.displayName.equalsIgnoreCase(name.trim())) {
                return s;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return displayName;
    }
}
