package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.*;
import com.bytex.customercaresystem.service.PartRequestService;
import com.bytex.customercaresystem.service.RepairService;
import com.bytex.customercaresystem.service.TicketService;
import com.bytex.customercaresystem.repository.PartRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/technician")
public class TechnicianController {

    private final TicketService ticketService;
    private final RepairService repairService;
    private final PartRequestService partRequestService;
    private final PartRepository partRepository;

    @Autowired
    public TechnicianController(TicketService ticketService, RepairService repairService, PartRequestService partRequestService, PartRepository partRepository) {
        this.ticketService = ticketService;
        this.repairService = repairService;
        this.partRequestService = partRequestService;
        this.partRepository = partRepository;
    }

    private boolean isNotTechnician(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"TECHNICIAN".equals(user.getRole().getRoleName())) {
            redirectAttributes.addFlashAttribute("error", "Access Denied");
            return true;
        }
        return false;
    }

    @GetMapping("/dashboard")
    public String technicianDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotTechnician(session, redirectAttributes)) return "redirect:/login";

        User technician = (User) session.getAttribute("user");
        List<Ticket> assignedTickets = ticketService.getTicketsByTechnician(technician);

        List<Ticket> filteredTickets = assignedTickets.stream()
                .filter(ticket -> {
                    com.bytex.customercaresystem.model.Repair repair = repairService.getRepairByTicket(ticket).orElse(null);
                    return repair == null || repair.getStatus() != com.bytex.customercaresystem.model.Repair.RepairStatus.ARCHIVED;
                })
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("tickets", filteredTickets);
        return "technician/dashboard";
    }

    @GetMapping("/tickets/{id}")
    public String viewTicket(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotTechnician(session, redirectAttributes)) return "redirect:/login";

        Ticket ticket = ticketService.getTicketById(id).orElseThrow(() -> new RuntimeException("Ticket not found"));
        Repair repair = repairService.getRepairByTicket(ticket).orElse(new Repair());

        model.addAttribute("ticket", ticket);
        model.addAttribute("repair", repair);
        model.addAttribute("partRequest", new PartRequest());
        model.addAttribute("parts", partRepository.findAll());
        return "technician/view_ticket";
    }

    @PostMapping("/repairs/save")
    public String saveRepair(@ModelAttribute Repair repair, @RequestParam Long ticketId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotTechnician(session, redirectAttributes)) return "redirect:/login";

        User technician = (User) session.getAttribute("user");
        Ticket ticket = ticketService.getTicketById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));

        if (repair.getRepairId() == null) {
            repairService.createRepair(repair, ticket, technician);
            redirectAttributes.addFlashAttribute("success", "Repair record created.");
        } else {
            repairService.updateRepair(repair.getRepairId(), repair);
            redirectAttributes.addFlashAttribute("success", "Repair record updated.");
        }
        return "redirect:/technician/tickets/" + ticketId;
    }

    @GetMapping("/part-requests")
    public String viewPartRequests(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotTechnician(session, redirectAttributes)) return "redirect:/login";

        User technician = (User) session.getAttribute("user");
        model.addAttribute("partRequests", partRequestService.getPartRequestsByRequestor(technician));
        return "technician/part_requests";
    }

    @PostMapping("/part-requests/new")
    public String createPartRequest(@ModelAttribute PartRequest partRequest, @RequestParam Long repairId, @RequestParam Long partId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotTechnician(session, redirectAttributes)) return "redirect:/login";

        User technician = (User) session.getAttribute("user");
        Repair repair = repairService.getRepairById(repairId).orElseThrow(() -> new RuntimeException("Repair not found"));
        Part part = partRepository.findById(partId).orElseThrow(() -> new RuntimeException("Part not found"));

        partRequest.setRequestor(technician);
        partRequest.setRepair(repair);
        partRequest.setPart(part);

        partRequestService.createPartRequest(partRequest);
        redirectAttributes.addFlashAttribute("success", "Part request submitted successfully.");
        return "redirect:/technician/tickets/" + repair.getTicket().getTicketId();
    }

    @PostMapping("/part-requests/{id}/cancel")
    public String cancelPartRequest(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotTechnician(session, redirectAttributes)) return "redirect:/login";

        User technician = (User) session.getAttribute("user");
        try {
            partRequestService.cancelPartRequest(id, technician);
            redirectAttributes.addFlashAttribute("success", "Part request canceled successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/technician/part-requests";
    }

    @PostMapping("/repairs/{id}/archive")
    public String archiveRepair(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotTechnician(session, redirectAttributes)) return "redirect:/login";

        try {
            repairService.archiveRepair(id);
            redirectAttributes.addFlashAttribute("success", "Repair record archived successfully.");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/technician/dashboard";
    }
}