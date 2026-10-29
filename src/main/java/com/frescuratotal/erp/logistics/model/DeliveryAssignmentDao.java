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

/** JDBC DAO for the {@code asignaciones_entrega} table. */
public class DeliveryAssignmentDao {

    public Integer create(DeliveryAssignment e) {
        String sql = "INSERT INTO asignaciones_entrega (id_ruta, id_vehiculo, id_conductor, "
                + "usuario_asignacion, notas) VALUES (?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getRouteId());
            ps.setInt(2, e.getVehicleId());
            ps.setInt(3, e.getDriverId());
            ps.setString(4, e.getAssignedBy());
            ps.setString(5, e.getNotes());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear asignacion: " + ex.getMessage(), ex);
        }
    }

    public List<DeliveryAssignment> findByRoute(int routeId) {
        String sql = "SELECT a.*, r.nombre_ruta, v.placa, co.nombre AS conductor "
                + "FROM asignaciones_entrega a "
                + "JOIN rutas_entrega r ON a.id_ruta = r.id_ruta "
                + "JOIN vehiculos v ON a.id_vehiculo = v.id_vehiculo "
                + "JOIN conductores co ON a.id_conductor = co.id_conductor WHERE a.id_ruta = ?";
        List<DeliveryAssignment> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, routeId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DeliveryAssignment a = new DeliveryAssignment();
                    a.setId(rs.getInt("id_asignacion"));
                    a.setRouteId(rs.getInt("id_ruta"));
                    a.setVehicleId(rs.getInt("id_vehiculo"));
                    a.setDriverId(rs.getInt("id_conductor"));
                    a.setAssignedBy(rs.getString("usuario_asignacion"));
                    a.setNotes(rs.getString("notas"));
                    a.setRouteName(rs.getString("nombre_ruta"));
                    a.setVehiclePlate(rs.getString("placa"));
                    a.setDriverName(rs.getString("conductor"));
                    list.add(a);
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar asignaciones: " + ex.getMessage(), ex);
        }
        return list;
    }
}
