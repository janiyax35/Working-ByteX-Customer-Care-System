package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.repository.PartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;

    @Autowired
    public PartServiceImpl(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Override
    public Part createPart(Part part) {
        // In a real application, you might check for duplicate part numbers here
        return partRepository.save(part);
    }

    @Override
    public Optional<Part> getPartById(Long id) {
        return partRepository.findById(id);
    }

    @Override
    public List<Part> getAllParts() {
        return partRepository.findAll();
    }

    @Override
    public Part updatePart(Long id, Part partDetails) {
        Part existingPart = partRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part not found with id: " + id));

        existingPart.setPartNumber(partDetails.getPartNumber());
        existingPart.setPartName(partDetails.getPartName());
        existingPart.setDescription(partDetails.getDescription());
        existingPart.setCategory(partDetails.getCategory());
        existingPart.setUnitPrice(partDetails.getUnitPrice());
        existingPart.setCurrentStock(partDetails.getCurrentStock());
        existingPart.setMinimumStock(partDetails.getMinimumStock());
        existingPart.setStatus(partDetails.getStatus());

        return partRepository.save(existingPart);
    }

    @Override
    public void deletePart(Long id) {
        if (!partRepository.existsById(id)) {
            throw new RuntimeException("Part not found with id: " + id);
        }
        partRepository.deleteById(id);
    }
}