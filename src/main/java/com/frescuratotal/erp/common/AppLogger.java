package com.frescuratotal.erp.common;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Centralized file logger for the whole application.
 *
 * <p>This is the single place where runtime events are persisted to disk. It uses
 * {@link java.util.logging} with a {@link FileHandler} that appends to
 * {@code logs/erp.log} (the {@code logs/} folder is created on first use through
 * {@link java.nio.file.Files}). Every line carries a timestamp and a severity
 * level. Centralizing logging here keeps the controllers focused on business
 * rules (SRP) and gives a single audit trail for the warehouse operation.</p>
 *
 * <p>The class is implemented as a lazily-initialized singleton: there is exactly
 * one {@link Logger} and one open {@link FileHandler} for the lifetime of the
 * process, mirroring the Singleton approach already used for the database
 * connection.</p>
 */
public final class AppLogger {

    /** Directory (relative to the working dir) where log files are written. */
    public static final Path LOG_DIR = Paths.get("logs");
    /** Main log file. */
    public static final Path LOG_FILE = LOG_DIR.resolve("erp.log");

    private static final DateTimeFormatter TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private static Logger logger;

    private AppLogger() {
    }

    /**
     * Returns the shared logger, creating the {@code logs/} directory and the file
     * handler on first access. If the file handler cannot be created, logging
     * degrades gracefully to the console instead of crashing the application.
     */
    private static synchronized Logger logger() {
        if (logger != null) {
            return logger;
        }
        Logger l = Logger.getLogger("com.frescuratotal.erp");
        l.setUseParentHandlers(false);
        l.setLevel(Level.ALL);
        try {
            if (!Files.exists(LOG_DIR)) {
                Files.createDirectories(LOG_DIR);
            }
            // append = true so events accumulate across runs (file handling).
            FileHandler handler = new FileHandler(LOG_FILE.toString(), true);
            handler.setFormatter(new PlainFormatter());
            handler.setLevel(Level.ALL);
            l.addHandler(handler);
        } catch (IOException ex) {
            // Fallback: keep running even if the log file cannot be opened.
            System.out.println("[LOG] No se pudo abrir el archivo de log: " + ex.getMessage());
        }
        logger = l;
        return l;
    }

    /** Logs an informational business event (e.g. a sale or a lot reception). */
    public static void info(String message) {
        logger().info(message);
    }

    /** Logs a warning (e.g. a business rule that blocked an operation). */
    public static void warning(String message) {
        logger().warning(message);
    }

    /** Logs an error together with its exception stack trace. */
    public static void error(String message, Throwable error) {
        logger().log(Level.SEVERE, message, error);
    }

    /** Logs an error message without an exception. */
    public static void error(String message) {
        logger().log(Level.SEVERE, message);
    }

    /**
     * Reads the most recent log lines straight from the file using
     * {@link java.nio.file.Files}. Used by the console UI to let the operator
     * review the audit trail without leaving the application.
     *
     * @param maxLines maximum number of trailing lines to return
     * @return the last {@code maxLines} lines, oldest first (empty if no log yet)
     */
    public static List<String> tail(int maxLines) {
        try {
            if (!Files.exists(LOG_FILE)) {
                return List.of();
            }
            List<String> all = Files.readAllLines(LOG_FILE, StandardCharsets.UTF_8);
            // Stream API: keep only the trailing window of lines.
            int from = Math.max(0, all.size() - maxLines);
            return all.subList(from, all.size());
        } catch (IOException ex) {
            throw new UncheckedIOException("No se pudo leer el archivo de log", ex);
        }
    }

    /** Simple one-line formatter: {@code [yyyy-MM-dd HH:mm:ss] LEVEL message}. */
    private static final class PlainFormatter extends Formatter {
        @Override
        public String format(LogRecord record) {
            String ts = LocalDateTime.now().format(TIMESTAMP);
            String base = String.format("[%s] %-7s %s%n",
                    ts, record.getLevel().getName(), record.getMessage());
            if (record.getThrown() != null) {
                base += "    -> " + record.getThrown() + System.lineSeparator();
            }
            return base;
        }
    }
}
