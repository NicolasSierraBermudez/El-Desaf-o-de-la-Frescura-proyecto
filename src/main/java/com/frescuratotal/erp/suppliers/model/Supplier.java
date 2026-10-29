package com.frescuratotal.erp.suppliers.model;

import java.time.LocalDateTime;

/**
 * Supplier entity. Maps the Spanish table {@code proveedores}.
 *
 * <p>Column mapping (DB -> field): id_proveedor -> id, nombre -> name,
 * nit -> nit, persona_contacto -> contactPerson, email -> email,
 * telefono -> phone, direccion -> address, activo -> active,
 * fecha_registro -> registrationDate.</p>
 */
public class Supplier {

    private int id;
    private String name;
    private String nit;
    private String contactPerson;
    private String email;
    private String phone;
    private String address;
    private boolean active = true;
    private LocalDateTime registrationDate;

    public Supplier() {
    }

    public Supplier(String name, String nit, String contactPerson, String email,
                    String phone, String address, boolean active) {
        this.name = name;
        this.nit = nit;
        this.contactPerson = contactPerson;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.active = active;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getContactPerson() {
        return contactPerson;
    }

    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (NIT: %s) - %s",
                id, name, nit, active ? "Activo" : "Inactivo");
    }
}
