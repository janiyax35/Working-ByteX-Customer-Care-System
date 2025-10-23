package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;

import java.util.List;
import java.util.Optional;

public interface TicketService {

    Ticket createTicket(Ticket ticket, User customer);

    Optional<Ticket> getTicketById(Long id);

    List<Ticket> getTicketsByCustomer(User customer);

    List<Ticket> getAllTickets();

    List<Ticket> getUnassignedOpenTickets();

    List<Ticket> getTicketsByStaff(User staff);

    List<Ticket> getTicketsByTechnician(User technician);

    Ticket updateTicket(Long id, Ticket ticketDetails);

    Ticket addResponseToTicket(Long ticketId, String message, User user);

    List<com.bytex.customercaresystem.model.Response> getResponsesForTicket(Ticket ticket);

    void cancelTicket(Long id, User customer);

    Ticket assignTicketToStaff(Long ticketId, Long staffId);

    Ticket assignTicketToTechnician(Long ticketId, Long technicianId);

    Ticket updateTicketStatus(Long ticketId, String status);

    void archiveTicket(Long ticketId);

}