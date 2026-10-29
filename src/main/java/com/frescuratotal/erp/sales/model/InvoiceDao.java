package com.frescuratotal.erp.sales.model;

import com.frescuratotal.erp.common.GenericDao;
import java.util.Optional;

/** DAO contract for {@link Invoice}. (DIP) */
public interface InvoiceDao extends GenericDao<Invoice, Integer> {

    /** @return the invoice associated with a sale, if already issued. */
    Optional<Invoice> findBySale(int saleId);
}
