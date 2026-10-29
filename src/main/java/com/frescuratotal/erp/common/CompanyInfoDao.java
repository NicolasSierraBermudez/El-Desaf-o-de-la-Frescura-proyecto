package com.frescuratotal.erp.common;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/** JDBC DAO for the {@code informacion_empresa} table. */
public class CompanyInfoDao {

    /**
     * Returns the first registered company, or a sensible default if the DB has
     * no record (keeps invoice generation working even with empty data).
     */
    public CompanyInfo getCompany() {
        String sql = "SELECT * FROM informacion_empresa ORDER BY id_empresa LIMIT 1";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                CompanyInfo ci = new CompanyInfo();
                ci.setId(rs.getInt("id_empresa"));
                ci.setCompanyName(rs.getString("nombre_empresa"));
                ci.setNit(rs.getString("nit"));
                ci.setAddress(rs.getString("direccion"));
                ci.setCity(rs.getString("ciudad"));
                ci.setPhone(rs.getString("telefono"));
                ci.setEmail(rs.getString("email"));
                return ci;
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al leer informacion de empresa: " + ex.getMessage(), ex);
        }
        return CompanyInfo.defaultCompany();
    }
}
