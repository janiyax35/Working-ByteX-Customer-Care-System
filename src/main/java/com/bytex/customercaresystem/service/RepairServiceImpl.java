package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Repair;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.RepairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class RepairServiceImpl implements RepairService {

    private final RepairRepository repairRepository;

    @Autowired
    public RepairServiceImpl(RepairRepository repairRepository) {
        this.repairRepository = repairRepository;
    }

    @Override
    public Repair createRepair(Repair repair, Ticket ticket, User technician) {
        repair.setTicket(ticket);
        repair.setTechnician(technician);
        repair.setStartDate(LocalDateTime.now());
        repair.setStatus(Repair.RepairStatus.PENDING);
        return repairRepository.save(repair);
    }

    @Override
    public Optional<Repair> getRepairById(Long id) {
        return repairRepository.findById(id);
    }

    @Override
    public Optional<Repair> getRepairByTicket(Ticket ticket) {
        return repairRepository.findByTicket(ticket);
    }

    @Override
    public List<Repair> getRepairsByTechnician(User technician) {
        return repairRepository.findByTechnician(technician);
    }

    @Override
    public Repair updateRepair(Long id, Repair repairDetails) {
        Repair existingRepair = repairRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair not found with id: " + id));

        existingRepair.setDiagnosis(repairDetails.getDiagnosis());
        existingRepair.setRepairDetails(repairDetails.getRepairDetails());
        existingRepair.setStatus(repairDetails.getStatus());

        if (repairDetails.getStatus() == Repair.RepairStatus.COMPLETED) {
            existingRepair.setCompletionDate(LocalDateTime.now());
        }

        return repairRepository.save(existingRepair);
    }

    @Override
    public void deleteRepair(Long id) {
        if (!repairRepository.existsById(id)) {
            throw new RuntimeException("Repair not found with id: " + id);
        }
        repairRepository.deleteById(id);
    }

    @Override
    public void archiveRepair(Long id) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair not found with id: " + id));

        if (repair.getStatus() != Repair.RepairStatus.COMPLETED) {
            throw new RuntimeException("Only completed repairs can be archived.");
        }

        repair.setStatus(Repair.RepairStatus.ARCHIVED);
        repairRepository.save(repair);
    }
}