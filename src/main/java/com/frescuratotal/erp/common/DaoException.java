package com.frescuratotal.erp.common;

/**
 * Unchecked exception wrapping low-level persistence errors (SQL failures).
 *
 * <p>DAOs translate {@link java.sql.SQLException} into this runtime exception so
 * that higher layers (controllers/views) are not forced to handle checked JDBC
 * exceptions, while still being able to react to connection problems with a
 * clear message.</p>
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    public DaoException(String message) {
        super(message);
    }
}
