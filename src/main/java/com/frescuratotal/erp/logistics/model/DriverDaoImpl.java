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

/** JDBC implementation of {@link DriverDao} for the {@code conductores} table. */
public class DriverDaoImpl implements DriverDao {

    private Driver map(ResultSet rs) throws SQLException {
        Driver d = new Driver();
        d.setId(rs.getInt("id_conductor"));
        d.setName(rs.getString("nombre"));
        d.setLicenseNumber(rs.getString("numero_licencia"));
        d.setPhone(rs.getString("telefono"));
        d.setEmail(rs.getString("email"));
        d.setActive(rs.getBoolean("activo"));
        return d;
    }

    @Override
    public Integer create(Driver e) {
        String sql = "INSERT INTO conductores (nombre, numero_licencia, telefono, email, activo) "
                + "VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getLicenseNumber());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getEmail());
            ps.setBoolean(5, e.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear conductor: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Driver> findById(Integer id) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM conductores WHERE id_conductor=?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar conductor: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Driver> findAll() {
        List<Driver> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM conductores ORDER BY nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar conductores: " + ex.getMessage(), ex);
        }
        return list;
    }

    @Override
    public boolean update(Driver e) {
        String sql = "UPDATE conductores SET nombre=?, numero_licencia=?, telefono=?, email=?, "
                + "activo=? WHERE id_conductor=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getLicenseNumber());
            ps.setString(3, e.getPhone());
            ps.setString(4, e.getEmail());
            ps.setBoolean(5, e.isActive());
            ps.setInt(6, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar conductor: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM conductores WHERE id_conductor=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al eliminar conductor: " + ex.getMessage(), ex);
        }
    }
}
