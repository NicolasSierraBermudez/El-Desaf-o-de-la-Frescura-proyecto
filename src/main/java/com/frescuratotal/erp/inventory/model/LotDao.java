package com.frescuratotal.erp.inventory.model;

import com.frescuratotal.erp.common.GenericDao;
import java.math.BigDecimal;
import java.util.List;

/**
 * DAO contract for {@link Lot}. Adds inventory-specific queries: available lots
 * for a product (FEFO ordering), status transitions and quantity updates.
 * (DIP: the inventory and sales controllers depend on this abstraction.)
 */
public interface LotDao extends GenericDao<Lot, Integer> {

    /** @return available lots of a product ordered by nearest expiration (FEFO). */
    List<Lot> findAvailableByProductFefo(int productId);

    /** @return all lots in a given status id. */
    List<Lot> findByStatus(int statusId);

    /** Updates only the status of a lot. */
    boolean updateStatus(int lotId, int statusId);

    /** Updates only the remaining quantity of a lot. */
    boolean updateQuantity(int lotId, BigDecimal newQuantity);

    /** @return lots belonging to a supplier (purchase history). */
    List<Lot> findBySupplier(int supplierId);
}
