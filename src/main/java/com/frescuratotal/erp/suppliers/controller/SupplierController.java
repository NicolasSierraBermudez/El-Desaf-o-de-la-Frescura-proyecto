package com.frescuratotal.erp.suppliers.controller;

import com.frescuratotal.erp.common.AppLogger;
import com.frescuratotal.erp.common.DaoFactory;
import com.frescuratotal.erp.inventory.model.Lot;
import com.frescuratotal.erp.inventory.model.LotDao;
import com.frescuratotal.erp.suppliers.model.Category;
import com.frescuratotal.erp.suppliers.model.CategoryDao;
import com.frescuratotal.erp.suppliers.model.Product;
import com.frescuratotal.erp.suppliers.model.ProductDao;
import com.frescuratotal.erp.suppliers.model.Supplier;
import com.frescuratotal.erp.suppliers.model.SupplierDao;
import com.frescuratotal.erp.suppliers.model.SupplierDaoImpl;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller for the suppliers &amp; purchases module (MVC: mediates between the
 * console views and the DAO layer). Holds no SQL; delegates persistence to DAOs
 * obtained from {@link DaoFactory} (DIP).
 */
public class SupplierController {

    private final SupplierDao supplierDao = DaoFactory.supplierDao();
    private final ProductDao productDao = DaoFactory.productDao();
    private final LotDao lotDao = DaoFactory.lotDao();
    private final CategoryDao categoryDao = new CategoryDao();

    public int registerSupplier(Supplier s) {
        int id = supplierDao.create(s);
        AppLogger.info("Proveedor registrado: " + s.getName() + " (id=" + id + ")");
        return id;
    }

    public boolean updateSupplier(Supplier s) {
        return supplierDao.update(s);
    }

    public List<Supplier> listSuppliers() {
        return supplierDao.findAll();
    }

    public Supplier findSupplier(int id) {
        return supplierDao.findById(id).orElse(null);
    }

    public int registerProduct(Product p) {
        return productDao.create(p);
    }

    public List<Product> listProducts() {
        return productDao.findAll();
    }

    public List<Category> listCategories() {
        return categoryDao.findAll();
    }

    /** Links a product to a supplier's catalog ({@code productos_proveedor}). */
    public void linkProductToSupplier(int supplierId, int productId) {
        ((SupplierDaoImpl) supplierDao).linkProduct(supplierId, productId);
    }

    /**
     * @return the products supplied by a supplier (catalog).
     *
     * <p>Resolves the catalog ids into products with a Stream pipeline:
     * {@code map(productDao::findById).flatMap(Optional::stream)}.</p>
     */
    public List<Product> productsOfSupplier(int supplierId) {
        return ((SupplierDaoImpl) supplierDao).findProductIds(supplierId).stream()
                .map(productDao::findById)
                .flatMap(java.util.Optional::stream)
                .collect(Collectors.toList());
    }

    /**
     * Report: counts how many products exist per category name, using the Stream
     * API with {@link Collectors#groupingBy} + {@link Collectors#counting}.
     *
     * @return a map {category name -> number of products}
     */
    public Map<String, Long> countProductsByCategory() {
        return productDao.findAll().stream()
                .collect(Collectors.groupingBy(
                        p -> p.getCategoryName() != null
                                ? p.getCategoryName() : ("Categoria " + p.getCategoryId()),
                        Collectors.counting()));
    }

    /** @return the purchase history (lots received) for a supplier. */
    public List<Lot> purchaseHistory(int supplierId) {
        return lotDao.findBySupplier(supplierId);
    }
}
