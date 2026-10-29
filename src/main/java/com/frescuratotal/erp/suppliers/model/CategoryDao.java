package com.frescuratotal.erp.suppliers.model;

import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.common.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC DAO for the {@code categorias_producto} catalog table. */
public class CategoryDao {

    private Category map(ResultSet rs) throws SQLException {
        return new Category(rs.getInt("id_categoria"),
                rs.getString("nombre"), rs.getString("descripcion"));
    }

    public List<Category> findAll() {
        String sql = "SELECT * FROM categorias_producto ORDER BY id_categoria";
        List<Category> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar categorias: " + ex.getMessage(), ex);
        }
        return list;
    }

    public Optional<Category> findById(int id) {
        String sql = "SELECT * FROM categorias_producto WHERE id_categoria = ?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar categoria: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }
}
