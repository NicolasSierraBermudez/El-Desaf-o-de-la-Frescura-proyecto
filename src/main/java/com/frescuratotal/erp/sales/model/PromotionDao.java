package com.frescuratotal.erp.sales.model;

import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.common.DatabaseConnection;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC DAO for the {@code promociones} table. */
public class PromotionDao {

    private Promotion map(ResultSet rs) throws SQLException {
        Promotion p = new Promotion();
        p.setId(rs.getInt("id_promocion"));
        p.setName(rs.getString("nombre"));
        p.setDescription(rs.getString("descripcion"));
        p.setDiscountRate(rs.getBigDecimal("tasa_descuento"));
        int estado = rs.getInt("aplica_estado_lote");
        p.setAppliesLotStatusId(rs.wasNull() ? null : estado);
        int cat = rs.getInt("aplica_categoria_producto");
        p.setAppliesCategoryId(rs.wasNull() ? null : cat);
        p.setActive(rs.getBoolean("activa"));
        Date fi = rs.getDate("fecha_inicio");
        if (fi != null) {
            p.setStartDate(fi.toLocalDate());
        }
        Date ff = rs.getDate("fecha_fin");
        if (ff != null) {
            p.setEndDate(ff.toLocalDate());
        }
        return p;
    }

    public List<Promotion> findAll() {
        return query("SELECT * FROM promociones ORDER BY id_promocion", -1);
    }

    /** Finds an active promotion that applies to a given lot status, if any. */
    public Optional<Promotion> findActiveByLotStatus(int lotStatusId) {
        String sql = "SELECT * FROM promociones WHERE activa = TRUE AND aplica_estado_lote = ? "
                + "ORDER BY tasa_descuento DESC LIMIT 1";
        List<Promotion> list = query(sql, lotStatusId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    private List<Promotion> query(String sql, int param) {
        List<Promotion> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (param >= 0) {
                ps.setInt(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al consultar promociones: " + ex.getMessage(), ex);
        }
        return list;
    }
}
