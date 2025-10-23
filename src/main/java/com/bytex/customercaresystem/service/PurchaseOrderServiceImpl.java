package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.OrderItem;
import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.PurchaseOrder;
import com.bytex.customercaresystem.model.WarehouseStock;
import com.bytex.customercaresystem.repository.PartRepository;
import com.bytex.customercaresystem.repository.PurchaseOrderRepository;
import com.bytex.customercaresystem.repository.WarehouseStockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PartRepository partRepository;
    private final WarehouseStockRepository warehouseStockRepository;

    @Autowired
    public PurchaseOrderServiceImpl(PurchaseOrderRepository purchaseOrderRepository, PartRepository partRepository, WarehouseStockRepository warehouseStockRepository) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.partRepository = partRepository;
        this.warehouseStockRepository = warehouseStockRepository;
    }

    @Override
    public PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder) {
        purchaseOrder.setOrderDate(LocalDateTime.now());
        purchaseOrder.setStatus(PurchaseOrder.OrderStatus.PENDING);

        BigDecimal totalAmount = BigDecimal.ZERO;
        if (purchaseOrder.getOrderItems() != null) {
            for (OrderItem item : purchaseOrder.getOrderItems()) {
                item.setPurchaseOrder(purchaseOrder);
                BigDecimal itemTotal = item.getUnitPrice().multiply(new BigDecimal(item.getQuantity()));
                totalAmount = totalAmount.add(itemTotal);
            }
        }
        purchaseOrder.setTotalAmount(totalAmount);

        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Override
    public Optional<PurchaseOrder> getPurchaseOrderById(Long id) {
        return purchaseOrderRepository.findById(id);
    }

    @Override
    public List<PurchaseOrder> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll();
    }

    @Override
    public PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder purchaseOrderDetails) {
        // This method is now only for updating details like expected delivery, not status.
        PurchaseOrder existingOrder = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with id: " + id));

        existingOrder.setSupplier(purchaseOrderDetails.getSupplier());
        existingOrder.setExpectedDelivery(purchaseOrderDetails.getExpectedDelivery());

        return purchaseOrderRepository.save(existingOrder);
    }

    @Override
    public void updateOrderStatus(Long orderId, PurchaseOrder.OrderStatus newStatus) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Purchase Order not found with id: " + orderId));

        // Only update stock if the status is changing to DELIVERED
        if (newStatus == PurchaseOrder.OrderStatus.DELIVERED && order.getStatus() != PurchaseOrder.OrderStatus.DELIVERED) {
            order.setActualDelivery(LocalDate.now());

            for (OrderItem item : order.getOrderItems()) {
                WarehouseStock stock = warehouseStockRepository.findById(item.getPart().getPartId())
                        .orElse(new WarehouseStock(item.getPart(), 0));
                stock.setQuantity(stock.getQuantity() + item.getQuantity());
                warehouseStockRepository.save(stock);
            }
        }
        order.setStatus(newStatus);
        purchaseOrderRepository.save(order);
    }

    @Override
    public void deletePurchaseOrder(Long id) {
        if (!purchaseOrderRepository.existsById(id)) {
            throw new RuntimeException("Purchase Order not found with id: " + id);
        }
        purchaseOrderRepository.deleteById(id);
    }
}