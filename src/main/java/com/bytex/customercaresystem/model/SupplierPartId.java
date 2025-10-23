package com.bytex.customercaresystem.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class SupplierPartId implements Serializable {

    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "part_id")
    private Long partId;

    // Constructors
    public SupplierPartId() {
    }

    public SupplierPartId(Long supplierId, Long partId) {
        this.supplierId = supplierId;
        this.partId = partId;
    }

    // Getters and Setters
    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    // hashCode and equals
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SupplierPartId that = (SupplierPartId) o;
        return Objects.equals(supplierId, that.supplierId) &&
               Objects.equals(partId, that.partId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(supplierId, partId);
    }
}