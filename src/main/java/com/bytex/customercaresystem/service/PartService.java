package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;

import java.util.List;
import java.util.Optional;

public interface PartService {
    Part createPart(Part part);
    Optional<Part> getPartById(Long id);
    List<Part> getAllParts();
    Part updatePart(Long id, Part partDetails);
    void deletePart(Long id);
}