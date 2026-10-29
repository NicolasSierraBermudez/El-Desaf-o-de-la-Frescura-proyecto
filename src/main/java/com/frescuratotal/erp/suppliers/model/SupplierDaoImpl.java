package com.frescuratotal.erp.suppliers.model;

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

/**
 * JDBC implementation of {@link SupplierDao} for the {@code proveedores} table.
 */
public class SupplierDaoImpl implements SupplierDao {

    private Supplier map(ResultSet rs) throws SQLException {
        Supplier s = new Supplier();
        s.setId(rs.getInt("id_proveedor"));
        s.setName(rs.getString("nombre"));
        s.setNit(rs.getString("nit"));
        s.setContactPerson(rs.getString("persona_contacto"));
        s.setEmail(rs.getString("email"));
        s.setPhone(rs.getString("telefono"));
        s.setAddress(rs.getString("direccion"));
        s.setActive(rs.getBoolean("activo"));
        Timestamp ts = rs.getTimestamp("fecha_registro");
        if (ts != null) {
            s.setRegistrationDate(ts.toLocalDateTime());
        }
        return s;
    }

    @Override
    public Integer create(Supplier e) {
        String sql = "INSERT INTO proveedores "
                + "(nombre, nit, persona_contacto, email, telefono, direccion, activo) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getNit());
            ps.setString(3, e.getContactPerson());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getPhone());
            ps.setString(6, e.getAddress());
            ps.setBoolean(7, e.isActive());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear proveedor: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Supplier> findById(Integer id) {
        String sql = "SELECT * FROM proveedores WHERE id_proveedor = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar proveedor: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Supplier> findByNit(String nit) {
        String sql = "SELECT * FROM proveedores WHERE nit = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nit);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar proveedor por NIT: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Supplier> findAll() {
        String sql = "SELECT * FROM proveedores ORDER BY nombre";
        List<Supplier> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar proveedores: " + ex.getMessage(), ex);
        }
        return list;
    }

    @Override
    public boolean update(Supplier e) {
        String sql = "UPDATE proveedores SET nombre=?, nit=?, persona_contacto=?, "
                + "email=?, telefono=?, direccion=?, activo=? WHERE id_proveedor=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getNit());
            ps.setString(3, e.getContactPerson());
            ps.setString(4, e.getEmail());
            ps.setString(5, e.getPhone());
            ps.setString(6, e.getAddress());
            ps.setBoolean(7, e.isActive());
            ps.setInt(8, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar proveedor: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM proveedores WHERE id_proveedor = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al eliminar proveedor: " + ex.getMessage(), ex);
        }
    }

    /**
     * Returns the ids of products supplied by a given supplier
     * (via {@code productos_proveedor}).
     *
     * @param supplierId supplier id
     * @return list of product ids
     */
    public List<Integer> findProductIds(int supplierId) {
        String sql = "SELECT id_producto FROM productos_proveedor WHERE id_proveedor = ?";
        List<Integer> ids = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar productos del proveedor: " + ex.getMessage(), ex);
        }
        return ids;
    }

    /**
     * Links a product to a supplier in {@code productos_proveedor}.
     *
     * @param supplierId supplier id
     * @param productId product id
     */
    public void linkProduct(int supplierId, int productId) {
        String sql = "INSERT IGNORE INTO productos_proveedor (id_proveedor, id_producto) VALUES (?, ?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, supplierId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Error al vincular producto a proveedor: " + ex.getMessage(), ex);
        }
    }
}
