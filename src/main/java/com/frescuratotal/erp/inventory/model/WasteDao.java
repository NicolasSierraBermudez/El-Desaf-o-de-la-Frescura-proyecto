package com.frescuratotal.erp.inventory.model;

import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.common.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** JDBC DAO for the {@code registro_mermas} table. */
public class WasteDao {

    private WasteRecord map(ResultSet rs) throws SQLException {
        WasteRecord w = new WasteRecord();
        w.setId(rs.getInt("id_registro"));
        w.setLotId(rs.getInt("id_lote"));
        int insp = rs.getInt("id_inspeccion");
        w.setInspectionId(rs.wasNull() ? null : insp);
        w.setLostQuantity(rs.getBigDecimal("cantidad_perdida"));
        w.setReason(rs.getString("motivo"));
        Timestamp d = rs.getTimestamp("fecha_registro");
        if (d != null) {
            w.setRegistrationDate(d.toLocalDateTime());
        }
        w.setRegisteredBy(rs.getString("usuario_registro"));
        try {
            w.setLotNumber(rs.getString("numero_lote"));
        } catch (SQLException ignored) {
            // present only on joined queries
        }
        return w;
    }

    public Integer create(WasteRecord e) {
        String sql = "INSERT INTO registro_mermas (id_lote, id_inspeccion, cantidad_perdida, "
                + "motivo, usuario_registro) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getLotId());
            if (e.getInspectionId() != null) {
                ps.setInt(2, e.getInspectionId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setBigDecimal(3, e.getLostQuantity());
            ps.setString(4, e.getReason());
            ps.setString(5, e.getRegisteredBy());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear merma: " + ex.getMessage(), ex);
        }
    }

    public List<WasteRecord> findAll() {
        String sql = "SELECT m.*, l.numero_lote FROM registro_mermas m "
                + "JOIN lotes_inventario l ON m.id_lote = l.id_lote "
                + "ORDER BY m.fecha_registro DESC";
        List<WasteRecord> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar mermas: " + ex.getMessage(), ex);
        }
        return list;
    }
}
