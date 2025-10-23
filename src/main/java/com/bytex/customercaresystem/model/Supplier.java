package com.bytex.customercaresystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

import java.util.Set;

@Entity
@Table(name = "suppliers")
public class Supplier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "supplier_id")
    private Long supplierId;

    @Column(name = "supplier_name", nullable = false, unique = true, length = 100)
    private String supplierName;

    @Column(name = "contact_info", length = 255)
    private String contactInfo;

    @Column(name = "address", columnDefinition = "TEXT")
    private String address;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupplierPart> suppliedParts = new java.util.HashSet<>();

    @OneToMany(mappedBy = "supplier", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PurchaseOrder> purchaseOrders = new java.util.HashSet<>();

    // Constructors
    public Supplier() {
    }

    // Getters and Setters for collections
    public Set<SupplierPart> getSuppliedParts() {
        return suppliedParts;
    }

    public void setSuppliedParts(Set<SupplierPart> suppliedParts) {
        this.suppliedParts = suppliedParts;
    }

    public Set<PurchaseOrder> getPurchaseOrders() {
        return purchaseOrders;
    }

    public void setPurchaseOrders(Set<PurchaseOrder> purchaseOrders) {
        this.purchaseOrders = purchaseOrders;
    }

    public Supplier(String supplierName, String contactInfo, String address) {
        this.supplierName = supplierName;
        this.contactInfo = contactInfo;
        this.address = address;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Supplier{" +
                "supplierId=" + supplierId +
                ", supplierName='" + supplierName + '\'' +
                '}';
    }
}