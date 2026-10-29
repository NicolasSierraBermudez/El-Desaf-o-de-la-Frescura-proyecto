package com.frescuratotal.erp.sales.model;

/**
 * Customer type catalog. Maps the Spanish table {@code tipos_cliente}.
 *
 * <p>Seed data: (1) Mayorista, (2) Minorista.</p>
 * <p>Column mapping: id_tipo -> id, nombre -> name, descripcion -> description.</p>
 */
public class CustomerType {

    private int id;
    private String name;
    private String description;

    public CustomerType() {
    }

    public CustomerType(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s", id, name);
    }
}
