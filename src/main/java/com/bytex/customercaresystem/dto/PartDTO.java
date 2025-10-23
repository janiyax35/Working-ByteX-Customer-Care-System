package com.bytex.customercaresystem.dto;

import java.math.BigDecimal;

public class PartDTO {

    private Long partId;
    private String partNumber;
    private String partName;
    private BigDecimal unitPrice;

    // Constructors
    public PartDTO() {
    }

    public PartDTO(Long partId, String partNumber, String partName, BigDecimal unitPrice) {
        this.partId = partId;
        this.partNumber = partNumber;
        this.partName = partName;
        this.unitPrice = unitPrice;
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

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}