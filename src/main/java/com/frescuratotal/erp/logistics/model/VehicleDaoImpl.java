package com.frescuratotal.erp.logistics.model;

import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.common.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of {@link VehicleDao} for the {@code vehiculos} table. */
public class VehicleDaoImpl implements VehicleDao {

    private Vehicle map(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setId(rs.getInt("id_vehiculo"));
        v.setPlate(rs.getString("placa"));
        v.setModel(rs.getString("modelo"));
        v.setCapacity(rs.getBigDecimal("capacidad"));
        v.setStatus(rs.getString("estado"));
        return v;
    }

    @Override
    public Integer create(Vehicle e) {
        String sql = "INSERT INTO vehiculos (placa, modelo, capacidad, estado) VALUES (?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getPlate());
            ps.setString(2, e.getModel());
            ps.setBigDecimal(3, e.getCapacity());
            ps.setString(4, e.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear vehiculo: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Vehicle> findById(Integer id) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM vehiculos WHERE id_vehiculo=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar vehiculo: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Vehicle> findAll() {
        List<Vehicle> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM vehiculos ORDER BY placa");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar vehiculos: " + ex.getMessage(), ex);
        }
        return list;
    }

    @Override
    public boolean update(Vehicle e) {
        String sql = "UPDATE vehiculos SET placa=?, modelo=?, capacidad=?, estado=? WHERE id_vehiculo=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getPlate());
            ps.setString(2, e.getModel());
            ps.setBigDecimal(3, e.getCapacity());
            ps.setString(4, e.getStatus());
            ps.setInt(5, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar vehiculo: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM vehiculos WHERE id_vehiculo=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al eliminar vehiculo: " + ex.getMessage(), ex);
        }
    }
}
