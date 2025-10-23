package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.TicketService;
import com.bytex.customercaresystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/staff")
public class StaffController {

    private final TicketService ticketService;
    private final UserService userService;
    private final com.bytex.customercaresystem.service.RepairService repairService;

    @Autowired
    public StaffController(TicketService ticketService, UserService userService, com.bytex.customercaresystem.service.RepairService repairService) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.repairService = repairService;
    }

    private boolean isNotStaff(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return true;
        }
        if (!"STAFF".equals(user.getRole().getRoleName())) {
            redirectAttributes.addFlashAttribute("error", "Access Denied");
            return true;
        }
        return false;
    }

    @GetMapping("/dashboard")
    public String staffDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotStaff(session, redirectAttributes)) return "redirect:/login";

        User staffMember = (User) session.getAttribute("user");
        model.addAttribute("tickets", ticketService.getTicketsByStaff(staffMember));
        model.addAttribute("user", staffMember);
        return "staff/dashboard";
    }

    @GetMapping("/tickets/{id}")
    public String viewTicket(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotStaff(session, redirectAttributes)) return "redirect:/login";

        Ticket ticket = ticketService.getTicketById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        // For assigning to technician
        List<User> technicians = userService.findAllUsers().stream()
                .filter(user -> "TECHNICIAN".equals(user.getRole().getRoleName()))
                .collect(Collectors.toList());

        model.addAttribute("ticket", ticket);
        model.addAttribute("technicians", technicians);
        repairService.getRepairByTicket(ticket).ifPresent(repair -> model.addAttribute("repair", repair));
        model.addAttribute("responses", ticketService.getResponsesForTicket(ticket));
        return "staff/view_ticket";
    }

    @GetMapping("/tickets/unassigned")
    public String viewUnassignedTickets(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotStaff(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("tickets", ticketService.getUnassignedOpenTickets());
        return "staff/unassigned_tickets";
    }

    @PostMapping("/tickets/{id}/assign-to-self")
    public String assignTicketToSelf(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotStaff(session, redirectAttributes)) return "redirect:/login";

        User staffMember = (User) session.getAttribute("user");
        try {
            ticketService.assignTicketToStaff(id, staffMember.getUserId());
            redirectAttributes.addFlashAttribute("success", "Ticket #" + id + " has been assigned to you.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/dashboard";
    }

    @PostMapping("/tickets/{id}/respond")
    public String addResponse(@PathVariable Long id, @RequestParam String message, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotStaff(session, redirectAttributes)) return "redirect:/login";

        if (message == null || message.trim().length() < 10) {
            redirectAttributes.addFlashAttribute("error", "Response message must be at least 10 characters long.");
            return "redirect:/staff/tickets/" + id;
        }

        User staffMember = (User) session.getAttribute("user");
        try {
            ticketService.addResponseToTicket(id, message, staffMember);
            redirectAttributes.addFlashAttribute("success", "Response sent successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/tickets/" + id;
    }

    @PostMapping("/tickets/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam String status, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotStaff(session, redirectAttributes)) return "redirect:/login";

        try {
            ticketService.updateTicketStatus(id, status);
            redirectAttributes.addFlashAttribute("success", "Ticket status updated.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/tickets/" + id;
    }

    @PostMapping("/tickets/{id}/assign")
    public String assignToTechnician(@PathVariable Long id, @RequestParam Long technicianId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotStaff(session, redirectAttributes)) return "redirect:/login";

        try {
            ticketService.assignTicketToTechnician(id, technicianId);
            redirectAttributes.addFlashAttribute("success", "Ticket assigned to technician.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/staff/tickets/" + id;
    }

    @PostMapping("/tickets/{id}/archive")
    public String archiveTicket(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotStaff(session, redirectAttributes)) return "redirect:/login";

        try {
            ticketService.archiveTicket(id);
            redirectAttributes.addFlashAttribute("success", "Ticket archived successfully.");
            return "redirect:/staff/dashboard";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/staff/tickets/" + id;
        }
    }
}