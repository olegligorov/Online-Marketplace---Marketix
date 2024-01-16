package com.marketix.service;

import com.marketix.entity.User;

public interface AuthService {
    User register(User user);
    User login(String email, String password);
}