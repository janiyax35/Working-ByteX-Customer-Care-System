package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.PartRequest;
import com.bytex.customercaresystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartRequestRepository extends JpaRepository<PartRequest, Long> {
    List<PartRequest> findByRequestor(User requestor);
}