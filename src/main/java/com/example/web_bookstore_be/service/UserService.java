package com.example.web_bookstore_be.service;

import com.example.web_bookstore_be.dao.UserRepository;
import com.example.web_bookstore_be.entity.Notification;
import com.example.web_bookstore_be.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    public ResponseEntity<?> register(User user) {
        // Kiểm tra username đã tồn tại chưa
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body(new Notification("Username đã tồn tại."));
        }

        // Kiểm tra email
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body(new Notification("Email đã tồn tại."));
        }

        // Mã hoá mật khẩu
        String encodePassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodePassword);

        // Lưu vào database
        userRepository.save(user);
        return ResponseEntity.ok("Đăng ký thành công!");
    }
}
