package com.marketix.service.impl;

import com.marketix.entity.User;
import com.marketix.service.AuthService;
import com.marketix.service.UserService;
import com.marketix.util.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserService userService;

    @Override
    public User register(User user) {
        if(user.getRole() == Role.ADMIN) {
            //#TODO fix the exception
            throw new RuntimeException("Admins can not self-register.");
        }
        return userService.createUser(user);
    }

    @Override
    public User login(String email, String password) {
        User user;
        try {
            user = userService.getUserByEmail(email);
        } catch (Exception e) {
            System.out.println("TUKA");
            System.out.println(e.getMessage());
            return null;
        }
        if(user.getPassword().equals(password)) {
            System.out.println("DO NOT MATCH");
            return user;
        } else {
            return null;
        }
    }

}
