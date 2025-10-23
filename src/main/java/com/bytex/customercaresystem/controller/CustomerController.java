package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.Ticket;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.TicketService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/customer")
public class CustomerController {

    private final TicketService ticketService;
    private final com.bytex.customercaresystem.service.RepairService repairService;

    @Autowired
    public CustomerController(TicketService ticketService, com.bytex.customercaresystem.service.RepairService repairService) {
        this.ticketService = ticketService;
        this.repairService = repairService;
    }

    private boolean isNotCustomer(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return true; // Not logged in
        }
        if (!"CUSTOMER".equals(user.getRole().getRoleName())) {
            redirectAttributes.addFlashAttribute("error", "Access Denied");
            return true; // Not a customer
        }
        return false;
    }

    @GetMapping("/dashboard")
    public String customerDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotCustomer(session, redirectAttributes)) return "redirect:/login";

        User customer = (User) session.getAttribute("user");
        model.addAttribute("tickets", ticketService.getTicketsByCustomer(customer));
        model.addAttribute("user", customer);
        return "customer/dashboard";
    }

    @GetMapping("/tickets/new")
    public String showCreateTicketForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotCustomer(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("ticket", new Ticket());
        return "customer/create_ticket";
    }

    @PostMapping("/tickets/new")
    public String createTicket(@ModelAttribute("ticket") Ticket ticket, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotCustomer(session, redirectAttributes)) return "redirect:/login";

        User customer = (User) session.getAttribute("user");
        ticketService.createTicket(ticket, customer);
        redirectAttributes.addFlashAttribute("success", "Ticket created successfully!");
        return "redirect:/customer/dashboard";
    }

    @GetMapping("/tickets/{id}")
    public String viewTicket(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotCustomer(session, redirectAttributes)) return "redirect:/login";

        Ticket ticket = ticketService.getTicketById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        User customer = (User) session.getAttribute("user");
        if (!ticket.getCustomer().getUserId().equals(customer.getUserId())) {
            redirectAttributes.addFlashAttribute("error", "Access Denied");
            return "redirect:/customer/dashboard";
        }

        model.addAttribute("ticket", ticket);
        model.addAttribute("responses", ticketService.getResponsesForTicket(ticket));
        repairService.getRepairByTicket(ticket).ifPresent(repair -> model.addAttribute("repair", repair));
        return "customer/view_ticket";
    }

    @PostMapping("/tickets/{id}/addComment")
    public String addComment(@PathVariable Long id, @RequestParam String message, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotCustomer(session, redirectAttributes)) return "redirect:/login";

        User customer = (User) session.getAttribute("user");
        try {
            ticketService.addResponseToTicket(id, message, customer);
            redirectAttributes.addFlashAttribute("success", "Your comment has been added.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/tickets/" + id;
    }


    @PostMapping("/tickets/{id}/cancel")
    public String cancelTicket(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotCustomer(session, redirectAttributes)) return "redirect:/login";

        try {
            User customer = (User) session.getAttribute("user");
            ticketService.cancelTicket(id, customer);
            redirectAttributes.addFlashAttribute("success", "Ticket canceled successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/customer/dashboard";
    }
}