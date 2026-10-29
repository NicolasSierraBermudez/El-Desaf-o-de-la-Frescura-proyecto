package com.frescuratotal.erp.logistics.model;

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
import java.util.Optional;

/** JDBC implementation of {@link DeliveryRouteDao} for {@code rutas_entrega}. */
public class DeliveryRouteDaoImpl implements DeliveryRouteDao {

    private DeliveryRoute map(ResultSet rs) throws SQLException {
        DeliveryRoute r = new DeliveryRoute();
        r.setId(rs.getInt("id_ruta"));
        r.setRouteName(rs.getString("nombre_ruta"));
        Timestamp fc = rs.getTimestamp("fecha_creacion");
        if (fc != null) {
            r.setCreationDate(fc.toLocalDateTime());
        }
        r.setCreatedBy(rs.getString("usuario_creacion"));
        Timestamp sde = rs.getTimestamp("hora_salida_estimada");
        if (sde != null) {
            r.setEstimatedDeparture(sde.toLocalDateTime());
        }
        Timestamp lle = rs.getTimestamp("hora_llegada_estimada");
        if (lle != null) {
            r.setEstimatedArrival(lle.toLocalDateTime());
        }
        Timestamp sdr = rs.getTimestamp("hora_salida_real");
        if (sdr != null) {
            r.setActualDeparture(sdr.toLocalDateTime());
        }
        Timestamp llr = rs.getTimestamp("hora_llegada_real");
        if (llr != null) {
            r.setActualArrival(llr.toLocalDateTime());
        }
        r.setStatus(rs.getString("estado"));
        return r;
    }

    @Override
    public Integer create(DeliveryRoute e) {
        String sql = "INSERT INTO rutas_entrega (nombre_ruta, usuario_creacion, estado) VALUES (?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getRouteName());
            ps.setString(2, e.getCreatedBy());
            ps.setString(3, e.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear ruta: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<DeliveryRoute> findById(Integer id) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM rutas_entrega WHERE id_ruta=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar ruta: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public List<DeliveryRoute> findAll() {
        List<DeliveryRoute> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM rutas_entrega ORDER BY fecha_creacion DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar rutas: " + ex.getMessage(), ex);
        }
        return list;
    }

    @Override
    public boolean update(DeliveryRoute e) {
        String sql = "UPDATE rutas_entrega SET nombre_ruta=?, estado=? WHERE id_ruta=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getRouteName());
            ps.setString(2, e.getStatus());
            ps.setInt(3, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar ruta: " + ex.getMessage(), ex);
        }
    }

    public boolean updateStatus(int routeId, String status) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("UPDATE rutas_entrega SET estado=? WHERE id_ruta=?")) {
            ps.setString(1, status);
            ps.setInt(2, routeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar estado de ruta: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM rutas_entrega WHERE id_ruta=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al eliminar ruta: " + ex.getMessage(), ex);
        }
    }
}
