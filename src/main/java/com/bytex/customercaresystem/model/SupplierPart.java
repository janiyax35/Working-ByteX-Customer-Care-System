package com.bytex.customercaresystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "supplier_parts")
public class SupplierPart {

    @EmbeddedId
    private SupplierPartId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("supplierId")
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("partId")
    @JoinColumn(name = "part_id")
    private Part part;

    // Constructors
    public SupplierPart() {
    }

    public SupplierPart(Supplier supplier, Part part) {
        this.supplier = supplier;
        this.part = part;
        this.id = new SupplierPartId(supplier.getSupplierId(), part.getPartId());
    }

    // Getters and Setters
    public SupplierPartId getId() {
        return id;
    }

    public void setId(SupplierPartId id) {
        this.id = id;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }
}