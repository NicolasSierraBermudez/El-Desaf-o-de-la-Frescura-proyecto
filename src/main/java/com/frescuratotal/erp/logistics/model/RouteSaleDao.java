package com.frescuratotal.erp.logistics.model;

import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.common.DatabaseConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/** JDBC DAO for the {@code ventas_ruta} table. */
public class RouteSaleDao {

    public Integer create(RouteSale e) {
        String sql = "INSERT INTO ventas_ruta (id_ruta, id_venta, orden_entrega, direccion_destino) "
                + "VALUES (?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getRouteId());
            ps.setInt(2, e.getSaleId());
            if (e.getDeliveryOrder() != null) {
                ps.setInt(3, e.getDeliveryOrder());
            } else {
                ps.setNull(3, Types.INTEGER);
            }
            ps.setString(4, e.getDestinationAddress());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al vincular venta a ruta: " + ex.getMessage(), ex);
        }
    }

    public List<RouteSale> findByRoute(int routeId) {
        String sql = "SELECT vr.*, v.numero_venta FROM ventas_ruta vr "
                + "JOIN ventas v ON vr.id_venta = v.id_venta WHERE vr.id_ruta = ? "
                + "ORDER BY vr.orden_entrega";
        List<RouteSale> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    RouteSale r = new RouteSale();
                    r.setId(rs.getInt("id"));
                    r.setRouteId(rs.getInt("id_ruta"));
                    r.setSaleId(rs.getInt("id_venta"));
                    int ord = rs.getInt("orden_entrega");
                    r.setDeliveryOrder(rs.wasNull() ? null : ord);
                    r.setDestinationAddress(rs.getString("direccion_destino"));
                    r.setReceivedBy(rs.getString("recibido_por"));
                    r.setObservations(rs.getString("observaciones"));
                    r.setSaleNumber(rs.getString("numero_venta"));
                    list.add(r);
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar ventas de ruta: " + ex.getMessage(), ex);
        }
        return list;
    }
}
