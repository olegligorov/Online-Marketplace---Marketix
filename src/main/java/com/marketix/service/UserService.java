package com.marketix.service;

import com.marketix.entity.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();

    User getUserById(Long id);

    User getUserByEmail(String username);

    User createUser(User user);
    User updateUser(User user);
    User deleteUser(Long id);
}
