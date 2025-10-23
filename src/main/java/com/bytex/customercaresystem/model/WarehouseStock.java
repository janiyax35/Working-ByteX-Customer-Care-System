package com.bytex.customercaresystem.model;

import jakarta.persistence.*;

@Entity
@Table(name = "warehouse_stock")
public class WarehouseStock {

    @Id
    @Column(name = "part_id")
    private Long partId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    // Constructors
    public WarehouseStock() {
    }

    public WarehouseStock(Part part, int quantity) {
        this.part = part;
        this.quantity = quantity;
    }

    // Getters and Setters
    public Long getPartId() {
        return partId;
    }

    public Part getPart() {
        return part;
    }

    public void setPart(Part part) {
        this.part = part;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}