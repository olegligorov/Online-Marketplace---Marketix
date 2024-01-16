package com.marketix.service.impl;


import com.marketix.dao.UserRepository;
import com.marketix.entity.User;
import com.marketix.exception.UserNotFoundException;
import com.marketix.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id %d not found", id)));
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException(String.format("User with id %s not found", email)));
    }

    @Override
    public User createUser(User user) {
        userRepository.findByEmail(user.getEmail()).ifPresent(x -> {
            //#TODO fix the exception, create a specified one
            throw new RuntimeException(String.format("User with email '%s' already exists.", user.getEmail()));
        });

        var now = LocalDateTime.now();
        user.setCreated(now);
        user.setModified(now);
        return userRepository.save(user);
    }

    @Override
    public User updateUser(User user) {
        var oldUser = getUserById(user.getId());
        user.setCreated(oldUser.getCreated());
        user.setModified(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User deleteUser(Long id) {
        var deleted = getUserById(id);
        userRepository.deleteById(id);
        return deleted;
    }
}
