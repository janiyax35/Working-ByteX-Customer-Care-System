package com.bytex.customercaresystem.repository;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByCustomer(User customer);
    List<Ticket> findByStaff(User staff);
    List<Ticket> findByTechnician(User technician);
    List<Ticket> findByStatusAndStaffIsNull(Ticket.TicketStatus status);
}