package com.example.web_bookstore_be.service.user;

import com.example.web_bookstore_be.dao.RoleRepository;
import com.example.web_bookstore_be.dao.UserRepository;
import com.example.web_bookstore_be.entity.Notification;
import com.example.web_bookstore_be.entity.Role;
import com.example.web_bookstore_be.entity.User;
import com.example.web_bookstore_be.service.UploadImage.UploadImageService;
import com.example.web_bookstore_be.service.email.EmailService;
import com.example.web_bookstore_be.service.util.Base64ToMultipartFileConverter;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.*;

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
    @Autowired
    private UploadImageService uploadImageService;
    private final ObjectMapper objectMapper;

    public UserServiceImp(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

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
        roleList.add(roleRepository.findByNameRole("CUSTOMER"));
        user.setListRoles(roleList);

        // Lưu vào database
        userRepository.save(user);

        // Gửi email cho người dùng để kích hoạt
        sendEmailActivation(user.getEmail(),user.getActivationCode());

        return ResponseEntity.ok("Đăng ký thành công!");
    }

    @Override
    public ResponseEntity<?> save(JsonNode userJson, String option) {
        try{
            User user = objectMapper.treeToValue(userJson, User.class);

            // Kiểm tra username đã tồn tại chưa
            if (!option.equals("update")) {
                if (userRepository.existsByUsername(user.getUsername())) {
                    return ResponseEntity.badRequest().body(new Notification("Username đã tồn tại."));
                }

                // Kiểm tra email
                if (userRepository.existsByEmail(user.getEmail())) {
                    return ResponseEntity.badRequest().body(new Notification("Email đã tồn tại."));
                }
            }

            // Set ngày sinh cho user
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
            Instant instant = Instant.from(formatter.parse(formatStringByJson(String.valueOf(userJson.get("dateOfBirth")))) );
            java.sql.Date dateOfBirth = new java.sql.Date(Date.from(instant).getTime());
            user.setDateOfBirth(dateOfBirth);

            // Set role cho user
            int idRoleRequest = Integer.parseInt(String.valueOf(userJson.get("role")));
            Optional<Role> role = roleRepository.findById(idRoleRequest);
            List<Role> roles = new ArrayList<>();
            roles.add(role.get());
            user.setListRoles(roles);

            // Mã hoá mật khẩu
            if (!(user.getPassword() == null)) { // Trường hợp là thêm hoặc thay đổi password
                String encodePassword = passwordEncoder.encode(user.getPassword());
                user.setPassword(encodePassword);
            } else {
                // Trường hợp cho update không thay đổi password
                Optional<User> userTemp = userRepository.findById(user.getIdUser());
                user.setPassword(userTemp.get().getPassword());
            }

            // Set avatar
            String avatar = (formatStringByJson(String.valueOf((userJson.get("avatar")))));
            if (avatar.length() > 500) {
                MultipartFile avatarFile = Base64ToMultipartFileConverter.convert(avatar);
                String avatarURL = uploadImageService.uploadImage(avatarFile, "User_" + user.getIdUser());
                user.setAvatar(avatarURL);
            }

            userRepository.save(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("thành công");
    }

    @Override
    public ResponseEntity<?> delete(int id) {
        try{
            Optional<User> user = userRepository.findById(id);

            if (user.isPresent()) {
                String imageUrl = user.get().getAvatar();

                if (imageUrl != null) {
                    uploadImageService.deleteImage(imageUrl);
                }

                userRepository.deleteById(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("thành công");
    }

    private String generateActivationCode() {
        return UUID.randomUUID().toString();
    }

    private void sendEmailActivation(String email, String activationCode) {
//        String endpointFE = "https://29da-2001-ee0-1b0b-b8e2-498f-8d3-462c-8984.ngrok-free.app";
        String endpointFE = "http//localhost:3000";
        String url = endpointFE + "/active/" + email + "/" + activationCode;
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
    private String formatStringByJson(String json) {
        return json.replaceAll("\"", "");
    }
}
