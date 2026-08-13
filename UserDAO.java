package com.jagadeesh.jagadeeshcart.dao;

import com.jagadeesh.jagadeeshcart.model.User;

import java.util.Optional;

public interface UserDAO {
    User save(User user);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Long id);
}
