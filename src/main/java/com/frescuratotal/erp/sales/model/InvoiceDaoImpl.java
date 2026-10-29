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

/** JDBC implementation of {@link InvoiceDao} for the {@code facturas} table. */
public class InvoiceDaoImpl implements InvoiceDao {

    private Invoice map(ResultSet rs) throws SQLException {
        Invoice i = new Invoice();
        i.setId(rs.getInt("id_factura"));
        i.setSaleId(rs.getInt("id_venta"));
        i.setInvoiceNumber(rs.getString("numero_factura"));
        i.setCufe(rs.getString("cufe"));
        i.setQrLink(rs.getString("enlace_qr"));
        Timestamp d = rs.getTimestamp("fecha_emision");
        if (d != null) {
            i.setIssueDate(d.toLocalDateTime());
        }
        i.setSubtotal(rs.getBigDecimal("subtotal"));
        i.setTotalDiscount(rs.getBigDecimal("total_descuento"));
        i.setTaxAmount(rs.getBigDecimal("monto_impuesto"));
        i.setTotalAmount(rs.getBigDecimal("monto_total"));
        return i;
    }

    @Override
    public Integer create(Invoice e) {
        String sql = "INSERT INTO facturas (id_venta, numero_factura, cufe, enlace_qr, subtotal, "
                + "total_descuento, monto_impuesto, monto_total) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, e.getSaleId());
            ps.setString(2, e.getInvoiceNumber());
            ps.setString(3, e.getCufe());
            ps.setString(4, e.getQrLink());
            ps.setBigDecimal(5, e.getSubtotal());
            ps.setBigDecimal(6, e.getTotalDiscount());
            ps.setBigDecimal(7, e.getTaxAmount());
            ps.setBigDecimal(8, e.getTotalAmount());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear factura: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Invoice> findBySale(int saleId) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM facturas WHERE id_venta = ?")) {
            ps.setInt(1, saleId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar factura por venta: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Invoice> findById(Integer id) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM facturas WHERE id_factura = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar factura: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Invoice> findAll() {
        List<Invoice> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("SELECT * FROM facturas ORDER BY fecha_emision DESC");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al listar facturas: " + ex.getMessage(), ex);
        }
        return list;
    }

    @Override
    public boolean update(Invoice e) {
        // Invoices are immutable once issued; no-op update kept for interface completeness.
        return false;
    }

    @Override
    public boolean delete(Integer id) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM facturas WHERE id_factura=?")) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al eliminar factura: " + ex.getMessage(), ex);
        }
    }
}
