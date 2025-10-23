package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.Role;
import com.bytex.customercaresystem.model.User;
import com.bytex.customercaresystem.repository.RoleRepository;
import com.bytex.customercaresystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ActivityLogService activityLogService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, ActivityLogService activityLogService) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.activityLogService = activityLogService;
    }

    @Override
    public User registerUser(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new RuntimeException("Username is already taken!");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email is already registered!");
        }

        Role userRole = roleRepository.findByRoleName("CUSTOMER");
        if (userRole == null) {
            // This is a fallback, ideally the role should always exist
            userRole = new Role("CUSTOMER");
            roleRepository.save(userRole);
        }
        user.setRole(userRole);
        user.setCreatedAt(LocalDateTime.now());
        User newUser = userRepository.save(user);
        activityLogService.logActivity("REGISTER", "USER", newUser.getUserId(), "New user registered: " + newUser.getUsername(), null);
        return newUser;
    }

    @Override
    public Optional<User> loginUser(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (password.equals(user.getPassword())) {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                activityLogService.logActivity("LOGIN", "USER", user.getUserId(), "User logged in: " + user.getUsername(), user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    @Override
    public User updateUser(Long id, User userDetails) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Prevent username/email conflicts
        userRepository.findByUsername(userDetails.getUsername()).ifPresent(user -> {
            if (!user.getUserId().equals(id)) {
                throw new RuntimeException("Username is already taken!");
            }
        });
        userRepository.findByEmail(userDetails.getEmail()).ifPresent(user -> {
            if (!user.getUserId().equals(id)) {
                throw new RuntimeException("Email is already registered!");
            }
        });

        existingUser.setUsername(userDetails.getUsername());
        existingUser.setEmail(userDetails.getEmail());
        existingUser.setFullName(userDetails.getFullName());
        existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
            existingUser.setPassword(userDetails.getPassword());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public User changeUserRole(Long userId, Integer roleId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RuntimeException("Role not found with id: " + roleId));

        user.setRole(role);
        return userRepository.save(user);
    }

    @Override
    public void updatePassword(User user, String newPassword) {
        user.setPassword(newPassword);
        userRepository.save(user);
        activityLogService.logActivity("PASSWORD_RESET", "USER", user.getUserId(), "User reset password for: " + user.getUsername(), user);
    }
}