package com.frescuratotal.erp.common;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

/**
 * Small helper for reading and validating console input (shared by all views).
 *
 * <p>Centralizing input parsing here keeps the views focused on presentation
 * (SRP) and provides consistent, null-safe prompts across modules.</p>
 */
public final class ConsoleUtils {

    private static final Scanner SCANNER = new Scanner(System.in);
    private static final DateTimeFormatter DATE_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private ConsoleUtils() {
    }

    /** Reads a line of text (may be empty). */
    public static String readString(String prompt) {
        System.out.print(prompt);
        return SCANNER.nextLine().trim();
    }

    /** Reads a non-empty line of text, re-prompting until one is provided. */
    public static String readRequiredString(String prompt) {
        String value;
        do {
            value = readString(prompt);
            if (value.isEmpty()) {
                System.out.println("  * El valor no puede estar vacio.");
            }
        } while (value.isEmpty());
        return value;
    }

    /** Reads an integer, re-prompting on invalid input. */
    public static int readInt(String prompt) {
        while (true) {
            String raw = readString(prompt);
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException ex) {
                System.out.println("  * Error: ingrese un número entero válido.");
            }
        }
    }

    /** Reads an integer within [min, max]. */
    public static int readInt(String prompt, int min, int max) {
        while (true) {
            int v = readInt(prompt);
            if (v >= min && v <= max) {
                return v;
            }
            System.out.printf("  * Ingrese un valor entre %d y %d.%n", min, max);
        }
    }

    /** Reads a BigDecimal, re-prompting on invalid input. */
    public static BigDecimal readDecimal(String prompt) {
        while (true) {
            String raw = readString(prompt);
            try {
                return new BigDecimal(raw);
            } catch (NumberFormatException ex) {
                System.out.println("  * Error: ingrese un número válido (ej: 1500.00).");
            }
        }
    }

    /** Reads a date in yyyy-MM-dd format. */
    public static LocalDate readDate(String prompt) {
        while (true) {
            String raw = readString(prompt + " (yyyy-MM-dd): ");
            try {
                return LocalDate.parse(raw, DATE_FMT);
            } catch (DateTimeParseException ex) {
                System.out.println("  * Formato de fecha invalido. Use yyyy-MM-dd.");
            }
        }
    }

    /** Reads a yes/no answer; returns true for "s"/"si"/"y"/"yes". */
    public static boolean readYesNo(String prompt) {
        String v = readString(prompt + " (s/n): ").toLowerCase();
        return v.equals("s") || v.equals("si") || v.equals("y") || v.equals("yes");
    }

    /** Pauses until the user presses Enter. */
    public static void pause() {
        System.out.print("\nPresione Enter para continuar...");
        SCANNER.nextLine();
    }
}
