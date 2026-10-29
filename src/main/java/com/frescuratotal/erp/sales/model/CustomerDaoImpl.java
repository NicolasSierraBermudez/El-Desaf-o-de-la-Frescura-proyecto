package com.frescuratotal.erp.sales.model;

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

/** JDBC implementation of {@link CustomerDao} for the {@code clientes} table. */
public class CustomerDaoImpl implements CustomerDao {

    private static final String SELECT_JOINED =
            "SELECT c.*, t.nombre AS tipo FROM clientes c "
            + "JOIN tipos_cliente t ON c.id_tipo = t.id_tipo ";

    private Customer map(ResultSet rs) throws SQLException {
        Customer c = new Customer();
        c.setId(rs.getInt("id_cliente"));
        c.setName(rs.getString("nombre"));
        c.setDocument(rs.getString("documento"));
        c.setEmail(rs.getString("email"));
        c.setPhone(rs.getString("telefono"));
        c.setAddress(rs.getString("direccion"));
        c.setTypeId(rs.getInt("id_tipo"));
        try {
            c.setTypeName(rs.getString("tipo"));
        } catch (SQLException ignored) {
            // present only on joined queries
        }
        return c;
    }

    @Override
    public Integer create(Customer e) {
        String sql = "INSERT INTO clientes (nombre, documento, email, telefono, direccion, id_tipo) "
                + "VALUES (?,?,?,?,?,?)";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getDocument());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getPhone());
            ps.setString(5, e.getAddress());
            ps.setInt(6, e.getTypeId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear cliente: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Customer> findById(Integer id) {
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_JOINED + "WHERE c.id_cliente = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar cliente: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_JOINED + "ORDER BY c.nombre");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar clientes: " + ex.getMessage(), ex);
        }
        return list;
    }

    @Override
    public boolean update(Customer e) {
        String sql = "UPDATE clientes SET nombre=?, documento=?, email=?, telefono=?, "
                + "direccion=?, id_tipo=? WHERE id_cliente=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getDocument());
            ps.setString(3, e.getEmail());
            ps.setString(4, e.getPhone());
            ps.setString(5, e.getAddress());
            ps.setInt(6, e.getTypeId());
            ps.setInt(7, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar cliente: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM clientes WHERE id_cliente=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al eliminar cliente: " + ex.getMessage(), ex);
        }
    }
}
