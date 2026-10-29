package com.frescuratotal.erp.inventory.model;

import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.common.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC DAO for the {@code registro_cuarentena} table. */
public class QuarantineDao {

    private QuarantineRecord map(ResultSet rs) throws SQLException {
        QuarantineRecord q = new QuarantineRecord();
        q.setId(rs.getInt("id_registro"));
        q.setLotId(rs.getInt("id_lote"));
        q.setReason(rs.getString("motivo"));
        q.setUser(rs.getString("usuario"));
        Timestamp reg = rs.getTimestamp("fecha_registro");
        if (reg != null) {
            q.setRegistrationDate(reg.toLocalDateTime());
        }
        q.setResolved(rs.getBoolean("resuelta"));
        Timestamp res = rs.getTimestamp("fecha_resolucion");
        if (res != null) {
            q.setResolutionDate(res.toLocalDateTime());
        }
        q.setResolutionNotes(rs.getString("notas_resolucion"));
        try {
            q.setLotNumber(rs.getString("numero_lote"));
        } catch (SQLException ignored) {
            // present only on joined queries
        }
        return q;
    }

    public Integer create(QuarantineRecord e) {
        String sql = "INSERT INTO registro_cuarentena (id_lote, motivo, usuario) VALUES (?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getLotId());
            ps.setString(2, e.getReason());
            ps.setString(3, e.getUser());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear registro de cuarentena: " + ex.getMessage(), ex);
        }
    }

    public List<QuarantineRecord> findAll() {
        String sql = "SELECT q.*, l.numero_lote FROM registro_cuarentena q "
                + "JOIN lotes_inventario l ON q.id_lote = l.id_lote "
                + "ORDER BY q.fecha_registro DESC";
        List<QuarantineRecord> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar cuarentenas: " + ex.getMessage(), ex);
        }
        return list;
    }

    /** Finds the open (unresolved) quarantine record for a lot, if any. */
    public Optional<QuarantineRecord> findOpenByLot(int lotId) {
        String sql = "SELECT q.*, l.numero_lote FROM registro_cuarentena q "
                + "JOIN lotes_inventario l ON q.id_lote = l.id_lote "
                + "WHERE q.id_lote = ? AND q.resuelta = FALSE ORDER BY q.fecha_registro DESC LIMIT 1";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, lotId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar cuarentena del lote: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    /** Marks the open quarantine of a lot as resolved. */
    public boolean resolveByLot(int lotId, String notes) {
        String sql = "UPDATE registro_cuarentena SET resuelta = TRUE, fecha_resolucion = ?, "
                + "notas_resolucion = ? WHERE id_lote = ? AND resuelta = FALSE";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(2, notes);
            ps.setInt(3, lotId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al resolver cuarentena: " + ex.getMessage(), ex);
        }
    }
}
