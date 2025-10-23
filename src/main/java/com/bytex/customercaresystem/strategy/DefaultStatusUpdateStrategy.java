package com.bytex.customercaresystem.strategy;

import com.bytex.customercaresystem.model.Part;
import org.springframework.stereotype.Component;

/**
 * The default strategy for updating a part's status based on stock levels.
 */
@Component
public class DefaultStatusUpdateStrategy implements PartStatusUpdateStrategy {

    @Override
    public void updateStatus(Part part) {
        if (part == null) {
            return;
        }

        // The DISCONTINUED status is managed manually and should not be automatically changed.
        if (part.getStatus() == Part.PartStatus.DISCONTINUED) {
            return;
        }

        if (part.getCurrentStock() <= 0) {
            part.setStatus(Part.PartStatus.OUT_OF_STOCK);
        } else if (part.getCurrentStock() < part.getMinimumStock()) {
            part.setStatus(Part.PartStatus.LOW_STOCK);
        } else {
            part.setStatus(Part.PartStatus.ACTIVE);
        }
    }
}