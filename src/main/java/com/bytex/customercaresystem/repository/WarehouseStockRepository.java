package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.WarehouseStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WarehouseStockRepository extends JpaRepository<WarehouseStock, Long> {
}