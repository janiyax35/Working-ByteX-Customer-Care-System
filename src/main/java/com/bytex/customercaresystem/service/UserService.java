package com.bytex.customercaresystem.service;

import com.bytex.customercaresystem.model.User;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User registerUser(User user);
    Optional<User> loginUser(String username, String password);
    User updateUser(Long id, User userDetails);
    Optional<User> findUserById(Long id);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    List<User> findAllUsers();
    void deleteUser(Long id);
    User changeUserRole(Long userId, Integer roleId);
    void updatePassword(User user, String newPassword);
}