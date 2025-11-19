package com.itis.oris.service;

import com.itis.oris.model.User;

public class AuthService {
    private final UserService userService;

    public AuthService(UserService userService) {
        this.userService = userService;
    }

    public User register(String username, String password, String email, String about) {
        return userService.register(username, password, email, about);
    }

    public User login(String username, String password) {
        return userService.login(username, password);
    }
}