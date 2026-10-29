package com.frescuratotal.erp.common;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Generates unique business numbers for lots, sales and invoices.
 *
 * <p>Formats:</p>
 * <ul>
 *   <li>Lot: {@code LOT-YYYYMMDD-####}</li>
 *   <li>Sale: {@code VEN-YYYYMMDD-####}</li>
 *   <li>Invoice: {@code FAC-YYYYMMDD-####}</li>
 * </ul>
 *
 * <p>SRP: this utility only knows how to build identifiers; uniqueness in the
 * database is additionally guaranteed by UNIQUE constraints on the columns.</p>
 */
public final class NumberGenerator {

    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyyMMdd");

    private NumberGenerator() {
        // Utility class.
    }

    /** @return a unique lot number, e.g. {@code LOT-20260115-0423}. */
    public static String nextLotNumber() {
        return build("LOT");
    }

    /** @return a unique sale number, e.g. {@code VEN-20260115-0423}. */
    public static String nextSaleNumber() {
        return build("VEN");
    }

    /** @return a unique invoice number, e.g. {@code FAC-20260115-0423}. */
    public static String nextInvoiceNumber() {
        return build("FAC");
    }

    private static String build(String prefix) {
        String date = LocalDate.now().format(DATE_FMT);
        int suffix = ThreadLocalRandom.current().nextInt(0, 10_000);
        return String.format("%s-%s-%04d", prefix, date, suffix);
    }
}
