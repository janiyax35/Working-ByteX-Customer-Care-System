package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.StockRequest;
import com.bytex.customercaresystem.model.WarehouseStock;
import com.bytex.customercaresystem.repository.PartRepository;
import com.bytex.customercaresystem.repository.StockRequestRepository;
import com.bytex.customercaresystem.repository.WarehouseStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class StockRequestServiceImpl implements StockRequestService {

    private final StockRequestRepository stockRequestRepository;
    private final WarehouseStockRepository warehouseStockRepository;
    private final PartRepository partRepository;
    private final PartStatusUpdaterService partStatusUpdaterService;

    @Autowired
    public StockRequestServiceImpl(StockRequestRepository stockRequestRepository, WarehouseStockRepository warehouseStockRepository, PartRepository partRepository, PartStatusUpdaterService partStatusUpdaterService) {
        this.stockRequestRepository = stockRequestRepository;
        this.warehouseStockRepository = warehouseStockRepository;
        this.partRepository = partRepository;
        this.partStatusUpdaterService = partStatusUpdaterService;
    }

    @Override
    public StockRequest createStockRequest(StockRequest stockRequest) {
        stockRequest.setRequestDate(LocalDateTime.now());
        stockRequest.setStatus(StockRequest.StockRequestStatus.PENDING);
        return stockRequestRepository.save(stockRequest);
    }

    @Override
    public List<StockRequest> getAllStockRequests() {
        return stockRequestRepository.findAll();
    }

    @Override
    public StockRequest fulfillStockRequest(Long id) {
        StockRequest stockRequest = stockRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock request not found with id: " + id));

        WarehouseStock warehouseStock = warehouseStockRepository.findById(stockRequest.getPart().getPartId())
                .orElseThrow(() -> new RuntimeException("Warehouse stock not found for this part."));

        if (warehouseStock.getQuantity() < stockRequest.getQuantityRequested()) {
            throw new RuntimeException("Not enough stock in warehouse to fulfill request.");
        }

        // Fulfill the request
        warehouseStock.setQuantity(warehouseStock.getQuantity() - stockRequest.getQuantityRequested());
        warehouseStockRepository.save(warehouseStock);

        Part part = stockRequest.getPart();
        part.setCurrentStock(part.getCurrentStock() + stockRequest.getQuantityRequested());

        // Use the strategy to update the part's status
        partStatusUpdaterService.updatePartStatus(part);

        partRepository.save(part);

        stockRequest.setStatus(StockRequest.StockRequestStatus.REFILLED);
        return stockRequestRepository.save(stockRequest);
    }
}