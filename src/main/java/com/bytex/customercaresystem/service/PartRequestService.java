package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.PartRequest;
import com.bytex.customercaresystem.model.User;

import java.util.List;
import java.util.Optional;

public interface PartRequestService {
    PartRequest createPartRequest(PartRequest partRequest);
    Optional<PartRequest> getPartRequestById(Long id);
    List<PartRequest> getAllPartRequests();
    List<PartRequest> getPartRequestsByRequestor(User requestor);
    PartRequest updatePartRequestStatus(Long id, String status);
    void deletePartRequest(Long id);
    void cancelPartRequest(Long id, User user);
}