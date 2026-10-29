package com.frescuratotal.erp.common;

/**
 * Company information entity. Maps the Spanish table {@code informacion_empresa}.
 *
 * <p>Used to populate the header of electronic invoices (NIT, name, address...).</p>
 * <p>Column mapping: id_empresa -> id, nombre_empresa -> companyName, nit -> nit,
 * direccion -> address, ciudad -> city, telefono -> phone, email -> email.</p>
 */
public class CompanyInfo {

    private int id;
    private String companyName;
    private String nit;
    private String address;
    private String city;
    private String phone;
    private String email;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /** @return a sensible default company used when the DB has no record. */
    public static CompanyInfo defaultCompany() {
        CompanyInfo c = new CompanyInfo();
        c.setCompanyName("Frescura Total S.A.S.");
        c.setNit("900123456-7");
        c.setAddress("Calle 123 #45-67");
        c.setCity("Bogotá");
        c.setPhone("(601) 234-5678");
        c.setEmail("contacto@frescuratotal.com");
        return c;
    }
}
