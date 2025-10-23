package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.PurchaseOrder;

import java.util.List;
import java.util.Optional;

public interface PurchaseOrderService {
    PurchaseOrder createPurchaseOrder(PurchaseOrder purchaseOrder);
    Optional<PurchaseOrder> getPurchaseOrderById(Long id);
    List<PurchaseOrder> getAllPurchaseOrders();
    PurchaseOrder updatePurchaseOrder(Long id, PurchaseOrder purchaseOrderDetails);
    void updateOrderStatus(Long orderId, PurchaseOrder.OrderStatus newStatus);
    void deletePurchaseOrder(Long id);
}