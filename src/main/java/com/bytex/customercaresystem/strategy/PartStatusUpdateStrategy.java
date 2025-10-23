package com.bytex.customercaresystem.strategy;

import com.bytex.customercaresystem.model.Part;

/**
 * Strategy interface for updating the status of a Part.
 * This allows different status update algorithms to be used interchangeably.
 */
public interface PartStatusUpdateStrategy {
    void updateStatus(Part part);
}