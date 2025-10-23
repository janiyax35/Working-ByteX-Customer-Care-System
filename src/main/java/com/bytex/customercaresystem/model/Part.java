package com.bytex.customercaresystem.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

import java.util.Set;

@Entity
@Table(name = "parts")
public class Part {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private Long partId;

    @Column(name = "part_number", nullable = false, unique = true, length = 50)
    private String partNumber;

    @Column(name = "part_name", nullable = false, length = 100)
    private String partName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "category", nullable = false, length = 50)
    private String category;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "current_stock", nullable = false)
    private Integer currentStock;

    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PartStatus status;

    public enum PartStatus {
        ACTIVE, LOW_STOCK, OUT_OF_STOCK, DISCONTINUED
    }

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<SupplierPart> suppliers;

    @OneToOne(mappedBy = "part", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private WarehouseStock warehouseStock;

    // Constructors
    public Part() {
    }

    // Getter and Setter for warehouseStock
    public WarehouseStock getWarehouseStock() {
        return warehouseStock;
    }

    public void setWarehouseStock(WarehouseStock warehouseStock) {
        this.warehouseStock = warehouseStock;
    }

    // Getters and Setters
    public Long getPartId() {
        return partId;
    }

    public void setPartId(Long partId) {
        this.partId = partId;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public String getPartName() {
        return partName;
    }

    public void setPartName(String partName) {
        this.partName = partName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(Integer currentStock) {
        this.currentStock = currentStock;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }

    public PartStatus getStatus() {
        return status;
    }

    public void setStatus(PartStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Part{" +
                "partId=" + partId +
                ", partNumber='" + partNumber + '\'' +
                ", partName='" + partName + '\'' +
                ", status=" + status +
                '}';
    }
}