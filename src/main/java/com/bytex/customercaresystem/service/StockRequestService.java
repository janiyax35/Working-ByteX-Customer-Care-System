package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.StockRequest;

import java.util.List;

public interface StockRequestService {
    StockRequest createStockRequest(StockRequest stockRequest);
    List<StockRequest> getAllStockRequests();
    StockRequest fulfillStockRequest(Long id);
}