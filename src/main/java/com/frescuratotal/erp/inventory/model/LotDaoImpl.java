package com.frescuratotal.erp.inventory.model;

import com.frescuratotal.erp.common.DaoException;
import com.frescuratotal.erp.common.DatabaseConnection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** JDBC implementation of {@link LotDao} for the {@code lotes_inventario} table. */
public class LotDaoImpl implements LotDao {

    private static final String SELECT_JOINED =
            "SELECT l.*, p.nombre AS producto, pr.nombre AS proveedor, e.nombre AS estado "
            + "FROM lotes_inventario l "
            + "JOIN productos p ON l.id_producto = p.id_producto "
            + "JOIN proveedores pr ON l.id_proveedor = pr.id_proveedor "
            + "JOIN estados_lote e ON l.id_estado = e.id_estado ";

    private Lot map(ResultSet rs) throws SQLException {
        Lot l = new Lot();
        l.setId(rs.getInt("id_lote"));
        l.setLotNumber(rs.getString("numero_lote"));
        l.setProductId(rs.getInt("id_producto"));
        l.setSupplierId(rs.getInt("id_proveedor"));
        Timestamp entry = rs.getTimestamp("fecha_ingreso");
        if (entry != null) {
            l.setEntryDate(entry.toLocalDateTime());
        }
        Date exp = rs.getDate("fecha_vencimiento");
        if (exp != null) {
            l.setExpirationDate(exp.toLocalDate());
        }
        l.setInitialQuantity(rs.getBigDecimal("cantidad_inicial"));
        l.setQuantity(rs.getBigDecimal("cantidad"));
        l.setPurchasePrice(rs.getBigDecimal("precio_compra"));
        l.setSalePrice(rs.getBigDecimal("precio_venta"));
        l.setAisle(rs.getString("pasillo"));
        l.setShelf(rs.getString("estante"));
        l.setStatusId(rs.getInt("id_estado"));
        try {
            l.setProductName(rs.getString("producto"));
            l.setSupplierName(rs.getString("proveedor"));
            l.setStatusName(rs.getString("estado"));
        } catch (SQLException ignored) {
            // Convenience columns only present on joined queries.
        }
        return l;
    }

    @Override
    public Integer create(Lot e) {
        String sql = "INSERT INTO lotes_inventario (numero_lote, id_producto, id_proveedor, "
                + "fecha_vencimiento, cantidad_inicial, cantidad, precio_compra, precio_venta, "
                + "pasillo, estante, id_estado) VALUES (?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getLotNumber());
            ps.setInt(2, e.getProductId());
            ps.setInt(3, e.getSupplierId());
            ps.setDate(4, Date.valueOf(e.getExpirationDate()));
            ps.setBigDecimal(5, e.getInitialQuantity());
            ps.setBigDecimal(6, e.getQuantity());
            ps.setBigDecimal(7, e.getPurchasePrice());
            ps.setBigDecimal(8, e.getSalePrice());
            ps.setString(9, e.getAisle());
            ps.setString(10, e.getShelf());
            ps.setInt(11, e.getStatusId());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    e.setId(keys.getInt(1));
                }
            }
            return e.getId();
        } catch (SQLException ex) {
            throw new DaoException("Error al crear lote: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Optional<Lot> findById(Integer id) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(SELECT_JOINED + "WHERE l.id_lote = ?")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al buscar lote: " + ex.getMessage(), ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Lot> findAll() {
        return query(SELECT_JOINED + "ORDER BY l.fecha_vencimiento", null);
    }

    @Override
    public List<Lot> findAvailableByProductFefo(int productId) {
        // Only Disponible (1) or Proximo a Vencer (2) lots with remaining quantity,
        // ordered by nearest expiration date (First Expire First Out).
        String sql = SELECT_JOINED
                + "WHERE l.id_producto = ? AND l.cantidad > 0 AND l.id_estado IN (1, 2) "
                + "ORDER BY l.fecha_vencimiento ASC";
        return query(sql, ps -> ps.setInt(1, productId));
    }

    @Override
    public List<Lot> findByStatus(int statusId) {
        return query(SELECT_JOINED + "WHERE l.id_estado = ? ORDER BY l.fecha_vencimiento",
                ps -> ps.setInt(1, statusId));
    }

    @Override
    public List<Lot> findBySupplier(int supplierId) {
        return query(SELECT_JOINED + "WHERE l.id_proveedor = ? ORDER BY l.fecha_ingreso DESC",
                ps -> ps.setInt(1, supplierId));
    }

    @Override
    public boolean updateStatus(int lotId, int statusId) {
        return exec("UPDATE lotes_inventario SET id_estado=? WHERE id_lote=?",
                ps -> { ps.setInt(1, statusId); ps.setInt(2, lotId); });
    }

    @Override
    public boolean updateQuantity(int lotId, BigDecimal newQuantity) {
        return exec("UPDATE lotes_inventario SET cantidad=? WHERE id_lote=?",
                ps -> { ps.setBigDecimal(1, newQuantity); ps.setInt(2, lotId); });
    }

    @Override
    public boolean update(Lot e) {
        String sql = "UPDATE lotes_inventario SET fecha_vencimiento=?, cantidad=?, "
                + "precio_compra=?, precio_venta=?, pasillo=?, estante=?, id_estado=? "
                + "WHERE id_lote=?";
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(e.getExpirationDate()));
            ps.setBigDecimal(2, e.getQuantity());
            ps.setBigDecimal(3, e.getPurchasePrice());
            ps.setBigDecimal(4, e.getSalePrice());
            ps.setString(5, e.getAisle());
            ps.setString(6, e.getShelf());
            ps.setInt(7, e.getStatusId());
            ps.setInt(8, e.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar lote: " + ex.getMessage(), ex);
        }
    }

    @Override
    public boolean delete(Integer id) {
        return exec("DELETE FROM lotes_inventario WHERE id_lote=?", ps -> ps.setInt(1, id));
    }

    // --- small functional helpers to reduce JDBC boilerplate ---

    @FunctionalInterface
    private interface Binder {
        void bind(PreparedStatement ps) throws SQLException;
    }

    private List<Lot> query(String sql, Binder binder) {
        List<Lot> list = new ArrayList<>();
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (binder != null) {
                binder.bind(ps);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al consultar lotes: " + ex.getMessage(), ex);
        }
        return list;
    }

    private boolean exec(String sql, Binder binder) {
        try (Connection c = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            binder.bind(ps);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar lote: " + ex.getMessage(), ex);
        }
    }
}
