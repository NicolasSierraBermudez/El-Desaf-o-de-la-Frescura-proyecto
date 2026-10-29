package com.frescuratotal.erp.sales.model;

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

/** JDBC implementation of {@link SaleDao} for the {@code ventas} table. */
public class SaleDaoImpl implements SaleDao {

    private static final String SELECT_JOINED =
            "SELECT v.*, c.nombre AS cliente, e.nombre AS estado FROM ventas v "
            + "JOIN clientes c ON v.id_cliente = c.id_cliente "
            + "JOIN estados_venta e ON v.id_estado = e.id_estado ";

    private Sale map(ResultSet rs) throws SQLException {
        Sale s = new Sale();
        s.setId(rs.getInt("id_venta"));
        s.setSaleNumber(rs.getString("numero_venta"));
        s.setCustomerId(rs.getInt("id_cliente"));
        Timestamp d = rs.getTimestamp("fecha_venta");
        if (d != null) {
            s.setSaleDate(d.toLocalDateTime());
        }
        s.setSubtotal(rs.getBigDecimal("subtotal"));
        s.setTotalDiscount(rs.getBigDecimal("total_descuento"));
        s.setTotalTax(rs.getBigDecimal("total_impuesto"));
        s.setTotalAmount(rs.getBigDecimal("monto_total"));
        s.setStatusId(rs.getInt("id_estado"));
        s.setRegisteredBy(rs.getString("usuario_registro"));
        try {
            s.setCustomerName(rs.getString("cliente"));
            s.setStatusName(rs.getString("estado"));
        } catch (SQLException ignored) {
            // joined-only columns
        }
        return s;
    }

    /**
     * Persists the sale, its details and decrements lot quantities atomically.
     */
    @Override
    public Integer create(Sale sale) {
        String saleSql = "INSERT INTO ventas (numero_venta, id_cliente, subtotal, total_descuento, "
                + "total_impuesto, monto_total, id_estado, usuario_registro) VALUES (?,?,?,?,?,?,?,?)";
        String detailSql = "INSERT INTO detalles_venta (id_venta, id_lote, cantidad_vendida, "
                + "precio_unitario, descuento_aplicado, monto_descuento, tasa_impuesto, "
                + "monto_impuesto, subtotal, total_linea) VALUES (?,?,?,?,?,?,?,?,?,?)";
        String lotSql = "UPDATE lotes_inventario SET cantidad = cantidad - ? WHERE id_lote = ?";

        Connection con = null;
        try {
            con = DatabaseConnection.getInstance().getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(saleSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, sale.getSaleNumber());
                ps.setInt(2, sale.getCustomerId());
                ps.setBigDecimal(3, sale.getSubtotal());
                ps.setBigDecimal(4, sale.getTotalDiscount());
                ps.setBigDecimal(5, sale.getTotalTax());
                ps.setBigDecimal(6, sale.getTotalAmount());
                ps.setInt(7, sale.getStatusId());
                ps.setString(8, sale.getRegisteredBy());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        sale.setId(keys.getInt(1));
                    }
                }
            }

            for (SaleDetail det : sale.getDetails()) {
                det.setSaleId(sale.getId());
                try (PreparedStatement ps = con.prepareStatement(detailSql)) {
                    ps.setInt(1, det.getSaleId());
                    ps.setInt(2, det.getLotId());
                    ps.setBigDecimal(3, det.getQuantitySold());
                    ps.setBigDecimal(4, det.getUnitPrice());
                    ps.setBigDecimal(5, det.getDiscountRate());
                    ps.setBigDecimal(6, det.getDiscountAmount());
                    ps.setBigDecimal(7, det.getTaxRate());
                    ps.setBigDecimal(8, det.getTaxAmount());
                    ps.setBigDecimal(9, det.getSubtotal());
                    ps.setBigDecimal(10, det.getLineTotal());
                    ps.executeUpdate();
                }
                try (PreparedStatement ps = con.prepareStatement(lotSql)) {
                    ps.setBigDecimal(1, det.getQuantitySold());
                    ps.setInt(2, det.getLotId());
                    ps.executeUpdate();
                }
            }

            con.commit();
            return sale.getId();
        } catch (SQLException ex) {
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ignored) {
                    // best effort rollback
                }
            }
            throw new DaoException("Error al registrar venta (transaccion revertida): "
                    + ex.getMessage(), ex);
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException ignored) {
                    // ignore on close
                }
            }
        }
    }

    @Override
    public Optional<Sale> findById(Integer id) {
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(SELECT_JOINED + "WHERE v.id_venta = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar venta: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public Sale findWithDetails(int saleId) {
        Sale sale = findById(saleId).orElse(null);
        if (sale == null) {
            return null;
        }
        String sql = "SELECT d.*, l.numero_lote, p.nombre AS producto FROM detalles_venta d "
                + "JOIN lotes_inventario l ON d.id_lote = l.id_lote "
                + "JOIN productos p ON l.id_producto = p.id_producto WHERE d.id_venta = ?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SaleDetail d = new SaleDetail();
                    d.setId(rs.getInt("id_detalle"));
                    d.setSaleId(rs.getInt("id_venta"));
                    d.setLotId(rs.getInt("id_lote"));
                    d.setQuantitySold(rs.getBigDecimal("cantidad_vendida"));
                    d.setUnitPrice(rs.getBigDecimal("precio_unitario"));
                    d.setDiscountRate(rs.getBigDecimal("descuento_aplicado"));
                    d.setDiscountAmount(rs.getBigDecimal("monto_descuento"));
                    d.setTaxRate(rs.getBigDecimal("tasa_impuesto"));
                    d.setTaxAmount(rs.getBigDecimal("monto_impuesto"));
                    d.setSubtotal(rs.getBigDecimal("subtotal"));
                    d.setLineTotal(rs.getBigDecimal("total_linea"));
                    d.setLotNumber(rs.getString("numero_lote"));
                    d.setProductName(rs.getString("producto"));
                    sale.getDetails().add(d);
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al cargar detalles de venta: " + ex.getMessage(), ex);
        }
        return sale;
    }

    @Override
    public List<Sale> findAll() {
        return queryList(SELECT_JOINED + "ORDER BY v.fecha_venta DESC", -1);
    }

    @Override
    public List<Sale> findByStatus(int statusId) {
        return queryList(SELECT_JOINED + "WHERE v.id_estado = ? ORDER BY v.fecha_venta DESC", statusId);
    }

    private List<Sale> queryList(String sql, int param) {
        List<Sale> list = new ArrayList<>();
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (param >= 0) {
                ps.setInt(1, param);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar ventas: " + ex.getMessage(), ex);
        }
        return list;
    }

    @Override
    public boolean updateStatus(int saleId, int statusId) {
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "UPDATE ventas SET id_estado=? WHERE id_venta=?")) {
            ps.setInt(1, statusId);
            ps.setInt(2, saleId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar estado de venta: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Integer> findCustomerIdsByLot(int lotId) {
        String sql = "SELECT DISTINCT v.id_cliente FROM detalles_venta d "
                + "JOIN ventas v ON d.id_venta = v.id_venta WHERE d.id_lote = ?";
        List<Integer> ids = new ArrayList<>();
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, lotId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ids.add(rs.getInt(1));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error en trazabilidad por lote: " + ex.getMessage(), ex);
        }
        return ids;
    }

    @Override
    public boolean update(Sale e) {
        String sql = "UPDATE ventas SET subtotal=?, total_descuento=?, total_impuesto=?, "
                + "monto_total=?, id_estado=? WHERE id_venta=?";
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setBigDecimal(1, e.getSubtotal());
            ps.setBigDecimal(2, e.getTotalDiscount());
            ps.setBigDecimal(3, e.getTotalTax());
            ps.setBigDecimal(4, e.getTotalAmount());
            ps.setInt(5, e.getStatusId());
            ps.setInt(6, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar venta: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection con = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM ventas WHERE id_venta=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al eliminar venta: " + ex.getMessage(), ex);
        }
    }
}
