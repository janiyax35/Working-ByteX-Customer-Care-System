package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.Part;
import com.bytex.customercaresystem.model.PartRequest;
import com.bytex.customercaresystem.model.StockRequest;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.PartRequestService;
import com.bytex.customercaresystem.service.PartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/productmanager")
public class ProductManagerController {

    private final PartRequestService partRequestService;
    private final PartService partService;
    private final com.bytex.customercaresystem.service.StockRequestService stockRequestService;

    @Autowired
    public ProductManagerController(PartRequestService partRequestService, PartService partService, com.bytex.customercaresystem.service.StockRequestService stockRequestService) {
        this.partRequestService = partRequestService;
        this.partService = partService;
        this.stockRequestService = stockRequestService;
    }

    private boolean isNotProductManager(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"PRODUCT_MANAGER".equals(user.getRole().getRoleName())) {
            redirectAttributes.addFlashAttribute("error", "Access Denied");
            return true;
        }
        return false;
    }

    @GetMapping("/dashboard")
    public String productManagerDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotProductManager(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("partRequests", partRequestService.getAllPartRequests().stream()
                .filter(pr -> pr.getStatus() == com.bytex.customercaresystem.model.PartRequest.RequestStatus.PENDING)
                .collect(java.util.stream.Collectors.toList()));
        return "productmanager/dashboard";
    }

    @PostMapping("/part-requests/{id}/status")
    public String updatePartRequestStatus(@PathVariable Long id, @RequestParam String status, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotProductManager(session, redirectAttributes)) return "redirect:/login";

        PartRequest request = partRequestService.getPartRequestById(id)
                .orElseThrow(() -> new RuntimeException("Part Request not found"));

        if ("APPROVED".equalsIgnoreCase(status)) {
            Part part = request.getPart();
            if (part.getCurrentStock() < request.getQuantity()) {
                redirectAttributes.addFlashAttribute("error", "Not enough stock to approve this request. Current stock: " + part.getCurrentStock());
                return "redirect:/productmanager/dashboard";
            }
            // Deduct stock
            part.setCurrentStock(part.getCurrentStock() - request.getQuantity());
            partService.updatePart(part.getPartId(), part);
        }

        partRequestService.updatePartRequestStatus(id, status);
        redirectAttributes.addFlashAttribute("success", "Part request status updated to " + status);
        return "redirect:/productmanager/dashboard";
    }

    @GetMapping("/inventory")
    public String viewInventory(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotProductManager(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("parts", partService.getAllParts());
        return "productmanager/inventory";
    }

    @GetMapping("/parts/new")
    public String showAddPartForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotProductManager(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("part", new Part());
        return "productmanager/part_form";
    }

    @GetMapping("/parts/edit/{id}")
    public String showEditPartForm(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotProductManager(session, redirectAttributes)) return "redirect:/login";

        Part part = partService.getPartById(id).orElseThrow(() -> new RuntimeException("Part not found"));
        model.addAttribute("part", part);
        return "productmanager/part_form";
    }

    @PostMapping("/parts/save")
    public String savePart(@ModelAttribute Part part, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotProductManager(session, redirectAttributes)) return "redirect:/login";

        if (part.getPartId() == null) {
            partService.createPart(part);
            redirectAttributes.addFlashAttribute("success", "Part added successfully.");
        } else {
            partService.updatePart(part.getPartId(), part);
            redirectAttributes.addFlashAttribute("success", "Part updated successfully.");
        }
        return "redirect:/productmanager/inventory";
    }

    @PostMapping("/parts/delete/{id}")
    public String deletePart(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotProductManager(session, redirectAttributes)) return "redirect:/login";

        try {
            partService.deletePart(id);
            redirectAttributes.addFlashAttribute("success", "Part removed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not delete part. It might be associated with existing requests or orders.");
        }
        return "redirect:/productmanager/inventory";
    }

    @PostMapping("/stock-requests/new")
    public String createStockRequest(@RequestParam Long partId, @RequestParam int quantity, @RequestParam String reason, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotProductManager(session, redirectAttributes)) return "redirect:/login";

        User productManager = (User) session.getAttribute("user");
        Part part = partService.getPartById(partId)
                .orElseThrow(() -> new RuntimeException("Part not found"));

        StockRequest stockRequest = new StockRequest();
        stockRequest.setPart(part);
        stockRequest.setQuantityRequested(quantity);
        stockRequest.setReason(reason);
        stockRequest.setRequestor(productManager);

        stockRequestService.createStockRequest(stockRequest);
        redirectAttributes.addFlashAttribute("success", "Stock request for '" + part.getPartName() + "' submitted successfully.");
        return "redirect:/productmanager/inventory";
    }

    @GetMapping("/stock-requests")
    public String viewStockRequests(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotProductManager(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("stockRequests", stockRequestService.getAllStockRequests());
        return "productmanager/stock_requests";
    }
}