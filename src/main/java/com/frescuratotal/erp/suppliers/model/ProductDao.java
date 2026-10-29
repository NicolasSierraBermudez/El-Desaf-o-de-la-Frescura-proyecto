package com.frescuratotal.erp.suppliers.model;

import com.frescuratotal.erp.common.GenericDao;

/** DAO contract for {@link Product}. (DIP: controllers depend on this abstraction.) */
public interface ProductDao extends GenericDao<Product, Integer> {
}
