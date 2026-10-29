package com.frescuratotal.erp.inventory.model;

import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.common.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/** JDBC DAO for the {@code inspecciones_calidad} table. */
public class InspectionDao {

    private QualityInspection map(ResultSet rs) throws SQLException {
        QualityInspection q = new QualityInspection();
        q.setId(rs.getInt("id_inspeccion"));
        q.setLotId(rs.getInt("id_lote"));
        Timestamp d = rs.getTimestamp("fecha_inspeccion");
        if (d != null) {
            q.setInspectionDate(d.toLocalDateTime());
        }
        q.setResult(rs.getString("resultado"));
        q.setInspectorName(rs.getString("nombre_inspector"));
        q.setRejectionReason(rs.getString("motivo_rechazo"));
        q.setNotes(rs.getString("notas"));
        try {
            q.setLotNumber(rs.getString("numero_lote"));
        } catch (SQLException ignored) {
            // present only on joined queries
        }
        return q;
    }

    public Integer create(QualityInspection e) {
        String sql = "INSERT INTO inspecciones_calidad (id_lote, resultado, nombre_inspector, "
                + "motivo_rechazo, notas) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getLotId());
            ps.setString(2, e.getResult());
            ps.setString(3, e.getInspectorName());
            ps.setString(4, e.getRejectionReason());
            ps.setString(5, e.getNotes());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear inspeccion: " + ex.getMessage(), ex);
        }
    }

    public List<QualityInspection> findAll() {
        String sql = "SELECT i.*, l.numero_lote FROM inspecciones_calidad i "
                + "JOIN lotes_inventario l ON i.id_lote = l.id_lote "
                + "ORDER BY i.fecha_inspeccion DESC";
        List<QualityInspection> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar inspecciones: " + ex.getMessage(), ex);
        }
        return list;
    }
}
