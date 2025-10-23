package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Repair;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;

import java.util.List;
import java.util.Optional;

public interface RepairService {
    Repair createRepair(Repair repair, Ticket ticket, User technician);
    Optional<Repair> getRepairById(Long id);
    Optional<Repair> getRepairByTicket(Ticket ticket);
    List<Repair> getRepairsByTechnician(User technician);
    Repair updateRepair(Long id, Repair repairDetails);
    void deleteRepair(Long id);
    void archiveRepair(Long id);
}