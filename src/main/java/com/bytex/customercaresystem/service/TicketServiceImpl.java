package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Response;
import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.ResponseRepository;
import com.bytex.customercaresystem.repository.TicketRepository;
import com.bytex.customercaresystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;
    private final ResponseRepository responseRepository;
    private final ActivityLogService activityLogService;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, UserRepository userRepository, ResponseRepository responseRepository, ActivityLogService activityLogService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.responseRepository = responseRepository;
        this.activityLogService = activityLogService;
    }

    @Override
    public List<Response> getResponsesForTicket(Ticket ticket) {
        return responseRepository.findByTicketOrderByCreatedAtAsc(ticket);
    }

    @Override
    public Ticket createTicket(Ticket ticket, User customer) {
        ticket.setCustomer(customer);
        ticket.setCreatedAt(LocalDateTime.now());
        ticket.setStatus(Ticket.TicketStatus.OPEN);
        ticket.setStage(Ticket.TicketStage.WITH_STAFF); // Default stage, but unassigned
        Ticket newTicket = ticketRepository.save(ticket);
        activityLogService.logActivity("CREATE_TICKET", "TICKET", newTicket.getTicketId(), "Ticket created by " + customer.getUsername(), customer);
        return newTicket;
    }

    @Override
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    @Override
    public List<Ticket> getTicketsByCustomer(User customer) {
        return ticketRepository.findByCustomer(customer);
    }

    @Override
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    @Override
    public List<Ticket> getUnassignedOpenTickets() {
        return ticketRepository.findByStatusAndStaffIsNull(Ticket.TicketStatus.OPEN);
    }

    @Override
    public List<Ticket> getTicketsByStaff(User staff) {
        return ticketRepository.findByStaff(staff);
    }

    @Override
    public List<Ticket> getTicketsByTechnician(User technician) {
        return ticketRepository.findByTechnician(technician);
    }

    @Override
    public Ticket updateTicket(Long id, Ticket ticketDetails) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        existingTicket.setSubject(ticketDetails.getSubject());
        existingTicket.setDescription(ticketDetails.getDescription());
        existingTicket.setPriority(ticketDetails.getPriority());
        existingTicket.setUpdatedAt(LocalDateTime.now());

        return ticketRepository.save(existingTicket);
    }

    @Override
    public Ticket addResponseToTicket(Long ticketId, String message, User user) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + ticketId));

        Response response = new Response();
        response.setTicket(ticket);
        response.setUser(user);
        response.setMessage(message);
        response.setCreatedAt(LocalDateTime.now());
        responseRepository.save(response);

        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    @Override
    public void cancelTicket(Long id, User customer) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found with id: " + id));

        if (!ticket.getCustomer().getUserId().equals(customer.getUserId())) {
            throw new RuntimeException("You are not authorized to cancel this ticket.");
        }

        if (ticket.getStatus() == Ticket.TicketStatus.OPEN || ticket.getStatus() == Ticket.TicketStatus.PENDING) {
            ticketRepository.delete(ticket);
        } else {
            throw new RuntimeException("Ticket cannot be canceled as it is already in progress or closed.");
        }
    }

    @Override
    public Ticket assignTicketToStaff(Long ticketId, Long staffId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff user not found"));

        if (!"STAFF".equals(staff.getRole().getRoleName())) {
            throw new RuntimeException("Cannot assign ticket: User is not a staff member.");
        }

        ticket.setStaff(staff);
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket assignTicketToTechnician(Long ticketId, Long technicianId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        User technician = userRepository.findById(technicianId)
                .orElseThrow(() -> new RuntimeException("Technician user not found"));

        if (!"TECHNICIAN".equals(technician.getRole().getRoleName())) {
            throw new RuntimeException("Cannot assign ticket: User is not a technician.");
        }

        ticket.setTechnician(technician);
        ticket.setStage(Ticket.TicketStage.WITH_TECHNICIAN);
        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    @Override
    public Ticket updateTicketStatus(Long ticketId, String status) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        Ticket.TicketStatus newStatus = Ticket.TicketStatus.valueOf(status.toUpperCase());
        ticket.setStatus(newStatus);

        if (newStatus == Ticket.TicketStatus.CLOSED) {
            ticket.setClosedAt(LocalDateTime.now());
            ticket.setStage(Ticket.TicketStage.RESOLVED);
        }

        ticket.setUpdatedAt(LocalDateTime.now());
        return ticketRepository.save(ticket);
    }

    @Override
    public void archiveTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (ticket.getStatus() != Ticket.TicketStatus.CLOSED) {
            throw new RuntimeException("Only closed tickets can be archived.");
        }

        ticket.setArchived(true);
        ticket.setArchivedAt(LocalDateTime.now());
        ticketRepository.save(ticket);
    }
}