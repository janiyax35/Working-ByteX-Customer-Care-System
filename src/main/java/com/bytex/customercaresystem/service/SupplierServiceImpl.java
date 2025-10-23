package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.dto.PartDTO;
import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.Supplier;
import com.bytex.customercaresystem.repository.SupplierRepository;
import com.bytex.customercaresystem.repository.SupplierPartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupplierServiceImpl implements SupplierService {

    private final SupplierRepository supplierRepository;
    private final SupplierPartRepository supplierPartRepository;

    @Autowired
    public SupplierServiceImpl(SupplierRepository supplierRepository, SupplierPartRepository supplierPartRepository) {
        this.supplierRepository = supplierRepository;
        this.supplierPartRepository = supplierPartRepository;
    }

    @Override
    public Supplier createSupplier(Supplier supplier) {
        supplier.setCreatedAt(LocalDateTime.now());
        return supplierRepository.save(supplier);
    }

    @Override
    public Optional<Supplier> getSupplierById(Long id) {
        return supplierRepository.findById(id);
    }

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier updateSupplier(Long id, Supplier supplierDetails) {
        Supplier existingSupplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + id));

        existingSupplier.setSupplierName(supplierDetails.getSupplierName());
        existingSupplier.setContactInfo(supplierDetails.getContactInfo());
        existingSupplier.setAddress(supplierDetails.getAddress());

        return supplierRepository.save(existingSupplier);
    }

    @Override
    public void deleteSupplier(Long id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found with id: " + id);
        }
        supplierRepository.deleteById(id);
    }

    @Override
    public List<PartDTO> findPartsBySupplier(Long supplierId) {
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found with id: " + supplierId));
        return supplierPartRepository.findBySupplier(supplier)
                .stream()
                .map(supplierPart -> {
                    Part part = supplierPart.getPart();
                    return new PartDTO(part.getPartId(), part.getPartNumber(), part.getPartName(), part.getUnitPrice());
                })
                .collect(Collectors.toList());
    }
}