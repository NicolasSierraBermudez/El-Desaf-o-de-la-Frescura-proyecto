package com.frescuratotal.erp.suppliers.model;

import com.frescuratotal.erp.common.GenericDao;
import java.util.Optional;

/**
 * DAO contract for {@link Supplier}. Extends the generic CRUD contract with a
 * lookup by NIT. (DIP: controllers depend on this interface, not the JDBC impl.)
 */
public interface SupplierDao extends GenericDao<Supplier, Integer> {

    /**
     * Finds a supplier by its NIT.
     *
     * @param nit tax id
     * @return optional supplier
     */
    Optional<Supplier> findByNit(String nit);
}
