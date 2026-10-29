package com.frescuratotal.erp.sales.model;

import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.common.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/** JDBC DAO for the {@code tipos_cliente} catalog table. */
public class CustomerTypeDao {

    public List<CustomerType> findAll() {
        String sql = "SELECT * FROM tipos_cliente ORDER BY id_tipo";
        List<CustomerType> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new CustomerType(rs.getInt("id_tipo"),
                        rs.getString("nombre"), rs.getString("descripcion")));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar tipos de cliente: " + ex.getMessage(), ex);
        }
        return list;
    }
}
