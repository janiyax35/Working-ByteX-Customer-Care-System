package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.StockRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRequestRepository extends JpaRepository<StockRequest, Long> {
}