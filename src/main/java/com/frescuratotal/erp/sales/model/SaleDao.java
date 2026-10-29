package com.frescuratotal.erp.sales.model;

import com.frescuratotal.erp.common.GenericDao;
import java.util.List;

/**
 * DAO contract for {@link Sale}. The create operation persists the sale header,
 * its line items and decrements the sold lot quantities in a single transaction.
 */
public interface SaleDao extends GenericDao<Sale, Integer> {

    /** @return sales in the given status id, with customer/status names. */
    List<Sale> findByStatus(int statusId);

    /** Updates only the status of a sale. */
    boolean updateStatus(int saleId, int statusId);

    /** @return the sale with its details loaded. */
    Sale findWithDetails(int saleId);

    /** @return customer ids that received products from a given lot (traceability). */
    List<Integer> findCustomerIdsByLot(int lotId);
}
