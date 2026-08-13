package com.jagadeesh.jagadeeshcart.service;

import com.jagadeesh.jagadeeshcart.dao.UserDAO;
import com.jagadeesh.jagadeeshcart.exception.AuthException;
import com.jagadeesh.jagadeeshcart.exception.ValidationException;
import com.jagadeesh.jagadeeshcart.model.User;
import com.jagadeesh.jagadeeshcart.util.PasswordUtil;
import com.jagadeesh.jagadeeshcart.util.ValidationUtil;

import java.util.Optional;

/**
 * Registration and login business logic.
 * Validation happens here, before any DAO call, per the API contract standard.
 */
public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User register(String name, String email, String password, String roleStr)
            throws ValidationException, AuthException {

        if (ValidationUtil.isBlank(name)) {
            throw new ValidationException("name", "Name is required");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new ValidationException("email", "A valid email is required");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new ValidationException("password", "Password must be at least 6 characters");
        }

        User.Role role;
        try {
            role = User.Role.valueOf(roleStr == null ? "" : roleStr.toUpperCase());
            if (role == User.Role.ADMIN) {
                // Admin is seeded only; no public admin signup (F1).
                throw new ValidationException("role", "Admin accounts cannot self-register");
            }
        } catch (IllegalArgumentException e) {
            throw new ValidationException("role", "Role must be BUYER or SELLER");
        }

        if (userDAO.findByEmail(email).isPresent()) {
            throw new AuthException("An account with this email already exists");
        }

        User user = new User();
        user.setName(name.trim());
        user.setEmail(email.trim().toLowerCase());
        user.setPasswordHash(PasswordUtil.hash(password));
        user.setRole(role);

        return userDAO.save(user);
    }

    public User login(String email, String password) throws AuthException, ValidationException {
        if (!ValidationUtil.isValidEmail(email) || ValidationUtil.isBlank(password)) {
            throw new ValidationException("email", "Email and password are required");
        }

        Optional<User> found = userDAO.findByEmail(email.trim().toLowerCase());
        if (found.isEmpty() || !PasswordUtil.matches(password, found.get().getPasswordHash())) {
            throw new AuthException("Invalid email or password");
        }
        return found.get();
    }
}
