package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.strategy.PartStatusUpdateStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Context class that uses a strategy to update a part's status.
 * This service is configured as a Spring bean, which is a Singleton by default.
 */
@Service
public class PartStatusUpdaterService {

    private final PartStatusUpdateStrategy statusUpdateStrategy;

    @Autowired
    public PartStatusUpdaterService(PartStatusUpdateStrategy statusUpdateStrategy) {
        this.statusUpdateStrategy = statusUpdateStrategy;
    }

    public void updatePartStatus(Part part) {
        statusUpdateStrategy.updateStatus(part);
    }
}