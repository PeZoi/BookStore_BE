package com.example.web_bookstore_be.service.user;

import com.example.web_bookstore_be.entity.User;
import org.springframework.http.ResponseEntity;

public interface UserService {
    public ResponseEntity<?> register(User user);
}
