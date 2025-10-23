package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.SupplierPart;
import com.bytex.customercaresystem.model.Supplier;
import com.bytex.customercaresystem.model.SupplierPart;
import com.bytex.customercaresystem.model.SupplierPartId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierPartRepository extends JpaRepository<SupplierPart, SupplierPartId> {
    List<SupplierPart> findBySupplier(Supplier supplier);
}