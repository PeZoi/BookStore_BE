package com.example.web_bookstore_be.service.user;

import com.example.web_bookstore_be.dao.RoleRepository;
import com.example.web_bookstore_be.dao.UserRepository;
import com.example.web_bookstore_be.entity.Notification;
import com.example.web_bookstore_be.entity.Role;
import com.example.web_bookstore_be.entity.User;
import com.example.web_bookstore_be.service.email.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImp implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private EmailService emailService;
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

        // Tạo mã kích hoạt cho người dùng
        user.setActivationCode(generateActivationCode());
        user.setEnabled(false);

        // Cho role mặc định
        List<Role> roleList = new ArrayList<>();
        roleList.add(roleRepository.findByNameRole("USER"));
        user.setListRoles(roleList);

        // Lưu vào database
        userRepository.save(user);

        // Gửi email cho người dùng để kích hoạt
        sendEmailActivation(user.getEmail(),user.getActivationCode());

        return ResponseEntity.ok("Đăng ký thành công!");
    }

    private String generateActivationCode() {
        return UUID.randomUUID().toString();
    }

    private void sendEmailActivation(String email, String activationCode) {
        String url = "http://localhost:3000/active/" + email + "/" + activationCode;
        String subject = "Kích hoạt tài khoản";
        String message = "Cảm ơn bạn đã là thành viên của chúng tôi. Vui lòng kích hoạt tài khoản!: <br/> Mã kích hoạt: <strong>"+ activationCode +"<strong/>";
        message += "<br/> Click vào đây để <a href="+ url +">kích hoạt</a>";
        try {
            emailService.sendMessage("dongph.0502@gmail.com", email, subject, message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<?> activeAccount(String email, String activationCode) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.badRequest().body(new Notification("Người dùng không tồn tại!"));
        }
        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body(new Notification("Tài khoản đã được kích hoạt"));
        }
        if (user.getActivationCode().equals(activationCode)) {
            user.setEnabled(true);
            userRepository.save(user);
        } else {
            return ResponseEntity.badRequest().body(new Notification("Mã kích hoạt không chính xác!"));
        }
        return ResponseEntity.ok("Kích hoạt thành công");
    }
}
