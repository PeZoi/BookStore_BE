package com.example.web_bookstore_be.controller;

import com.example.web_bookstore_be.entity.User;
import com.example.web_bookstore_be.security.JwtResponse;
import com.example.web_bookstore_be.security.LoginRequest;
import com.example.web_bookstore_be.service.JWT.JwtService;
import com.example.web_bookstore_be.service.UserSecurityService;
import com.example.web_bookstore_be.service.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin()
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserSecurityService userSecurityService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Validated @RequestBody User user) throws MessagingException {
        return userService.register(user);
    }

    @GetMapping("/active-account")
    public ResponseEntity<?> activeAccount(@RequestParam String email, @RequestParam String activationCode) {
        return userService.activeAccount(email, activationCode);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<?> authenticate (@RequestBody LoginRequest loginRequest) {
        // Xử lý xác thực người dùng
        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );
            // Nếu xác thực thành công
            if (authentication.isAuthenticated()) {
                final String jwtToken = jwtService.generateToken(loginRequest.getUsername());
                return ResponseEntity.ok(new JwtResponse(jwtToken));
            }
        } catch (AuthenticationException e) {
            return ResponseEntity.badRequest().body("Tên đăng nhập hoặc mật khẩu không đúng!");
        }
        return ResponseEntity.badRequest().body("Xác thực không thành công");
    }
}
