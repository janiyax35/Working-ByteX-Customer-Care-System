package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.PartRequest;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.PartRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PartRequestServiceImpl implements PartRequestService {

    private final PartRequestRepository partRequestRepository;

    @Autowired
    public PartRequestServiceImpl(PartRequestRepository partRequestRepository) {
        this.partRequestRepository = partRequestRepository;
    }

    @Override
    public PartRequest createPartRequest(PartRequest partRequest) {
        partRequest.setRequestDate(LocalDateTime.now());
        partRequest.setStatus(PartRequest.RequestStatus.PENDING);
        return partRequestRepository.save(partRequest);
    }

    @Override
    public Optional<PartRequest> getPartRequestById(Long id) {
        return partRequestRepository.findById(id);
    }

    @Override
    public List<PartRequest> getAllPartRequests() {
        return partRequestRepository.findAll();
    }

    @Override
    public List<PartRequest> getPartRequestsByRequestor(User requestor) {
        return partRequestRepository.findByRequestor(requestor);
    }

    @Override
    public PartRequest updatePartRequestStatus(Long id, String status) {
        PartRequest partRequest = partRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part request not found with id: " + id));

        PartRequest.RequestStatus newStatus = PartRequest.RequestStatus.valueOf(status.toUpperCase());
        partRequest.setStatus(newStatus);

        if (newStatus == PartRequest.RequestStatus.FULFILLED) {
            partRequest.setFulfillmentDate(LocalDateTime.now());
        }

        return partRequestRepository.save(partRequest);
    }

    @Override
    public void deletePartRequest(Long id) {
        if (!partRequestRepository.existsById(id)) {
            throw new RuntimeException("Part request not found with id: " + id);
        }
        partRequestRepository.deleteById(id);
    }

    @Override
    public void cancelPartRequest(Long id, User user) {
        PartRequest partRequest = partRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Part request not found with id: " + id));

        if (!partRequest.getRequestor().getUserId().equals(user.getUserId())) {
            throw new RuntimeException("You are not authorized to cancel this part request.");
        }

        if (partRequest.getStatus() != PartRequest.RequestStatus.PENDING) {
            throw new RuntimeException("Only pending part requests can be canceled.");
        }

        partRequestRepository.delete(partRequest);
    }
}