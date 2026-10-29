package com.frescuratotal.erp.common;

/**
 * Centralized database configuration (credentials and connection URL).
 *
 * <p>These are example values for a local MySQL 8 instance. Change them here to
 * match your own environment. This is the single place where connection
 * parameters live (SRP: this class only knows about configuration).</p>
 */
public final class DatabaseConfig {

    /** Database host. */
    public static final String HOST = "localhost";

    /** Database port. */
    public static final int PORT = 3306;

    /** Schema / database name created by docs/database.sql. */
    public static final String DATABASE = "erp_frescura_total";

    /** Database user. */
    public static final String USER = "root";

    /** Database password. */
    public static final String PASSWORD = "root";

    private DatabaseConfig() {
        // Utility class: no instances.
    }

    /**
     * Builds the JDBC connection URL from the configured parameters.
     *
     * @return a JDBC URL such as
     *         {@code jdbc:mysql://localhost:3306/erp_frescura_total}
     */
    public static String getJdbcUrl() {
        return "jdbc:mysql://" + HOST + ":" + PORT + "/" + DATABASE
                + "?useSSL=false&allowPublicKeyRetrieval=true"
                + "&serverTimezone=America/Bogota&characterEncoding=UTF-8";
    }
}
