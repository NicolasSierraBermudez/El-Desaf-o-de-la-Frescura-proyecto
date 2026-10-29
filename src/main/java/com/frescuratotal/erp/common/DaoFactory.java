package com.frescuratotal.erp.common;

import com.frescuratotal.erp.inventory.model.LotDao;
import com.frescuratotal.erp.inventory.model.LotDaoImpl;
import com.frescuratotal.erp.logistics.model.DeliveryRouteDao;
import com.frescuratotal.erp.logistics.model.DeliveryRouteDaoImpl;
import com.frescuratotal.erp.logistics.model.DriverDao;
import com.frescuratotal.erp.logistics.model.DriverDaoImpl;
import com.frescuratotal.erp.logistics.model.VehicleDao;
import com.frescuratotal.erp.logistics.model.VehicleDaoImpl;
import com.frescuratotal.erp.sales.model.CustomerDao;
import com.frescuratotal.erp.sales.model.CustomerDaoImpl;
import com.frescuratotal.erp.sales.model.InvoiceDao;
import com.frescuratotal.erp.sales.model.InvoiceDaoImpl;
import com.frescuratotal.erp.sales.model.SaleDao;
import com.frescuratotal.erp.sales.model.SaleDaoImpl;
import com.frescuratotal.erp.suppliers.model.ProductDao;
import com.frescuratotal.erp.suppliers.model.ProductDaoImpl;
import com.frescuratotal.erp.suppliers.model.SupplierDao;
import com.frescuratotal.erp.suppliers.model.SupplierDaoImpl;

/**
 * Factory that creates DAO instances.
 *
 * <p><b>Design pattern: Factory.</b> Controllers ask this factory for a DAO by
 * its interface type and receive a concrete JDBC implementation. This decouples
 * the business layer from the concrete persistence classes (DIP + OCP: swapping
 * the implementation only requires changing this factory, not every caller).</p>
 */
public final class DaoFactory {

    private DaoFactory() {
        // Static factory; no instances.
    }

    public static SupplierDao supplierDao() {
        return new SupplierDaoImpl();
    }

    public static ProductDao productDao() {
        return new ProductDaoImpl();
    }

    public static LotDao lotDao() {
        return new LotDaoImpl();
    }

    public static CustomerDao customerDao() {
        return new CustomerDaoImpl();
    }

    public static SaleDao saleDao() {
        return new SaleDaoImpl();
    }

    public static InvoiceDao invoiceDao() {
        return new InvoiceDaoImpl();
    }

    public static DriverDao driverDao() {
        return new DriverDaoImpl();
    }

    public static VehicleDao vehicleDao() {
        return new VehicleDaoImpl();
    }

    public static DeliveryRouteDao deliveryRouteDao() {
        return new DeliveryRouteDaoImpl();
    }
}
