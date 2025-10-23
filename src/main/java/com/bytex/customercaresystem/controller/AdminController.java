package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.Role;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.RoleRepository;
import com.bytex.customercaresystem.service.TicketService;
import com.bytex.customercaresystem.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final TicketService ticketService;
    private final RoleRepository roleRepository;
    private final com.bytex.customercaresystem.service.ActivityLogService activityLogService;

    @Autowired
    public AdminController(UserService userService, TicketService ticketService, RoleRepository roleRepository, com.bytex.customercaresystem.service.ActivityLogService activityLogService) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.roleRepository = roleRepository;
        this.activityLogService = activityLogService;
    }

    private boolean isNotAdmin(HttpSession session, RedirectAttributes redirectAttributes) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole().getRoleName())) {
            redirectAttributes.addFlashAttribute("error", "Access Denied");
            return true;
        }
        return false;
    }

    @GetMapping("/dashboard")
    public String adminDashboard(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("totalUsers", userService.findAllUsers().size());
        model.addAttribute("totalTickets", ticketService.getAllTickets().size());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String manageUsers(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("users", userService.findAllUsers());
        return "admin/users";
    }

    @GetMapping("/users/new")
    public String showCreateUserForm(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("user", new User());
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/user_form";
    }

    @GetMapping("/users/edit/{id}")
    public String showEditUserForm(@PathVariable Long id, HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session, redirectAttributes)) return "redirect:/login";

        User user = userService.findUserById(id).orElseThrow(() -> new RuntimeException("User not found"));
        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepository.findAll());
        return "admin/user_form";
    }

    @PostMapping("/users/save")
    public String saveUser(@ModelAttribute User user, @RequestParam Integer roleId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session, redirectAttributes)) return "redirect:/login";

        Role role = roleRepository.findById(roleId).orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRole(role);

        try {
            if (user.getUserId() == null) {
                userService.registerUser(user);
                redirectAttributes.addFlashAttribute("success", "User created successfully.");
            } else {
                userService.updateUser(user.getUserId(), user);
                userService.changeUserRole(user.getUserId(), roleId);
                redirectAttributes.addFlashAttribute("success", "User updated successfully.");
            }
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/users/new";
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session, redirectAttributes)) return "redirect:/login";

        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser.getUserId().equals(id)) {
            redirectAttributes.addFlashAttribute("error", "You cannot delete your own account.");
            return "redirect:/admin/users";
        }

        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("success", "User deleted successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Could not delete user. They may be associated with tickets or other records.");
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/tickets")
    public String monitorTickets(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("tickets", ticketService.getAllTickets());
        model.addAttribute("assignableUsers", userService.findAllUsers().stream()
                .filter(u -> "STAFF".equals(u.getRole().getRoleName()) || "TECHNICIAN".equals(u.getRole().getRoleName()))
                .collect(java.util.stream.Collectors.toList()));
        return "admin/tickets";
    }

    @PostMapping("/tickets/{id}/reassign")
    public String reassignTicket(@PathVariable Long id, @RequestParam Long userId, HttpSession session, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session, redirectAttributes)) return "redirect:/login";

        try {
            User userToAssign = userService.findUserById(userId)
                    .orElseThrow(() -> new RuntimeException("User to assign not found"));

            if ("STAFF".equals(userToAssign.getRole().getRoleName())) {
                ticketService.assignTicketToStaff(id, userId);
            } else if ("TECHNICIAN".equals(userToAssign.getRole().getRoleName())) {
                ticketService.assignTicketToTechnician(id, userId);
            } else {
                throw new RuntimeException("Can only assign tickets to STAFF or TECHNICIAN roles.");
            }

            redirectAttributes.addFlashAttribute("success", "Ticket reassigned successfully to " + userToAssign.getFullName());
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", "Failed to reassign ticket: " + e.getMessage());
        }
        return "redirect:/admin/tickets";
    }

    @GetMapping("/logs")
    public String viewActivityLogs(HttpSession session, Model model, RedirectAttributes redirectAttributes) {
        if (isNotAdmin(session, redirectAttributes)) return "redirect:/login";

        model.addAttribute("logs", activityLogService.getAllLogs());
        return "admin/activity_logs";
    }
}