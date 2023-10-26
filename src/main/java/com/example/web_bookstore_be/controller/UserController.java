package com.example.web_bookstore_be.controller;

import com.example.web_bookstore_be.entity.User;
import com.example.web_bookstore_be.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @CrossOrigin()
    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody User user) {
        return userService.register(user);
    }
}
