package com.frescuratotal.erp.suppliers.model;

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

/** JDBC implementation of {@link ProductDao} for the {@code productos} table. */
public class ProductDaoImpl implements ProductDao {

    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setId(rs.getInt("id_producto"));
        p.setName(rs.getString("nombre"));
        p.setDescription(rs.getString("descripcion"));
        p.setCategoryId(rs.getInt("id_categoria"));
        p.setUnitOfMeasure(rs.getString("unidad_medida"));
        p.setBasePrice(rs.getBigDecimal("precio_base"));
        try {
            String cat = rs.getString("categoria");
            if (cat != null) {
                p.setCategoryName(cat);
            }
        } catch (SQLException ignored) {
            // "categoria" column only present on joined queries.
        }
        return p;
    }

    @Override
    public Integer create(Product e) {
        String sql = "INSERT INTO productos (nombre, descripcion, id_categoria, unidad_medida, precio_base) "
                + "VALUES (?, ?, ?, ?, ?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getDescription());
            ps.setInt(3, e.getCategoryId());
            ps.setString(4, e.getUnitOfMeasure());
            ps.setBigDecimal(5, e.getBasePrice());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear producto: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Product> findById(Integer id) {
        String sql = "SELECT p.*, c.nombre AS categoria FROM productos p "
                + "JOIN categorias_producto c ON p.id_categoria = c.id_categoria "
                + "WHERE p.id_producto = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar producto: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        String sql = "SELECT p.*, c.nombre AS categoria FROM productos p "
                + "JOIN categorias_producto c ON p.id_categoria = c.id_categoria "
                + "ORDER BY p.nombre";
        List<Product> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar productos: " + ex.getMessage(), ex);
        }
        return list;
    }

    @Override
    public boolean update(Product e) {
        String sql = "UPDATE productos SET nombre=?, descripcion=?, id_categoria=?, "
                + "unidad_medida=?, precio_base=? WHERE id_producto=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, e.getName());
            ps.setString(2, e.getDescription());
            ps.setInt(3, e.getCategoryId());
            ps.setString(4, e.getUnitOfMeasure());
            ps.setBigDecimal(5, e.getBasePrice());
            ps.setInt(6, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar producto: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean delete(Integer id) {
        String sql = "DELETE FROM productos WHERE id_producto = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al eliminar producto: " + ex.getMessage(), ex);
        }
    }
}
