package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Supplier;

import java.util.List;
import java.util.Optional;

public interface SupplierService {
    Supplier createSupplier(Supplier supplier);
    Optional<Supplier> getSupplierById(Long id);
    List<Supplier> getAllSuppliers();
    Supplier updateSupplier(Long id, Supplier supplierDetails);
    void deleteSupplier(Long id);
    List<com.bytex.customercaresystem.dto.PartDTO> findPartsBySupplier(Long supplierId);
}