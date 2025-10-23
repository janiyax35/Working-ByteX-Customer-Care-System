package com.bytex.customercaresystem.controller;

import com.bytex.customercaresystem.model.PasswordResetToken;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.service.EmailService;
import com.bytex.customercaresystem.service.UserService;
import com.bytex.customercaresystem.repository.PasswordResetTokenRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
public class AuthController {

    private final UserService userService;
    private final EmailService emailService;
    private final PasswordResetTokenRepository tokenRepository;


    @Autowired
    public AuthController(UserService userService, EmailService emailService, PasswordResetTokenRepository tokenRepository) {
        this.userService = userService;
        this.emailService = emailService;
        this.tokenRepository = tokenRepository;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String showLoginPage(Model model) {
        model.addAttribute("user", new User());
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.loginUser(username, password);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            session.setAttribute("user", user);
            String role = user.getRole().getRoleName();
            switch (role) {
                case "ADMIN":
                    return "redirect:/admin/dashboard";
                case "STAFF":
                    return "redirect:/staff/dashboard";
                case "TECHNICIAN":
                    return "redirect:/technician/dashboard";
                case "PRODUCT_MANAGER":
                    return "redirect:/productmanager/dashboard";
                case "WAREHOUSE_MANAGER":
                    return "redirect:/warehousemanager/dashboard";
                case "CUSTOMER":
                    return "redirect:/customer/dashboard";
                default:
                    return "redirect:/login?error=Invalid Role";
            }
        } else {
            redirectAttributes.addFlashAttribute("error", "Invalid username or password");
            return "redirect:/login";
        }
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute("user") User user, RedirectAttributes redirectAttributes) {
        try {
            userService.registerUser(user);
            redirectAttributes.addFlashAttribute("success", "Registration successful! Please log in.");
            return "redirect:/login";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String showProfilePage(HttpSession session, Model model) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", sessionUser);
        return "profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("user") User user, HttpSession session, RedirectAttributes redirectAttributes) {
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return "redirect:/login";
        }
        try {
            User updatedUser = userService.updateUser(sessionUser.getUserId(), user);
            session.setAttribute("user", updatedUser); // Update session user
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        Optional<User> userOptional = userService.findByEmail(email);
        if (userOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "We couldn't find an account for that email address.");
            return "redirect:/forgot-password";
        }

        User user = userOptional.get();
        String token = UUID.randomUUID().toString();
        String otp = String.format("%06d", new java.util.Random().nextInt(999999));

        PasswordResetToken resetToken = new PasswordResetToken(token, user, otp, LocalDateTime.now().plusHours(1));
        tokenRepository.save(resetToken);

        String resetLink = "http://localhost:8080/reset-password?token=" + token;

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("name", user.getFullName());
        templateModel.put("otp", otp);
        templateModel.put("resetLink", resetLink);

        emailService.sendEmailWithHtmlTemplate(user.getEmail(), "Password Reset Request", "email/password-reset-email", templateModel);

        redirectAttributes.addFlashAttribute("message", "A password reset link has been sent to your email.");
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model, RedirectAttributes redirectAttributes) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired password reset token.");
            return "redirect:/forgot-password";
        }
        model.addAttribute("token", token);
        return "reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
                                       @RequestParam("otp") String otp,
                                       @RequestParam("password") String password,
                                       @RequestParam("confirmPassword") String confirmPassword,
                                       RedirectAttributes redirectAttributes) {

        PasswordResetToken resetToken = tokenRepository.findByToken(token);
        if (resetToken == null || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Invalid or expired password reset token.");
            return "redirect:/forgot-password";
        }

        if (!resetToken.getOtp().equals(otp)) {
            redirectAttributes.addFlashAttribute("error", "Invalid OTP.");
            return "redirect:/reset-password?token=" + token;
        }

        if (!password.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/reset-password?token=" + token;
        }

        User user = resetToken.getUser();
        userService.updatePassword(user, password);
        tokenRepository.delete(resetToken);

        redirectAttributes.addFlashAttribute("message", "You have successfully reset your password.");
        return "redirect:/login";
    }
}