package com.frescuratotal.erp.sales.model;

import java.time.LocalDateTime;

/**
 * Customer entity. Maps the Spanish table {@code clientes}.
 *
 * <p>Column mapping: id_cliente -> id, nombre -> name, documento -> document,
 * email -> email, telefono -> phone, direccion -> address, id_tipo -> typeId,
 * fecha_registro -> registrationDate.</p>
 */
public class Customer {

    private int id;
    private String name;
    private String document;
    private String email;
    private String phone;
    private String address;
    private int typeId;
    private String typeName; // convenience field from joins
    private LocalDateTime registrationDate;

    public Customer() {
    }

    public Customer(String name, String document, String email, String phone,
                    String address, int typeId) {
        this.name = name;
        this.document = document;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.typeId = typeId;
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

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
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

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s (Doc: %s) - %s",
                id, name, document, typeName != null ? typeName : ("Tipo " + typeId));
    }
}
