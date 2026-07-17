package mdl.proxysthproject.controller;

import mdl.proxysthproject.entity.EbUser;
import mdl.proxysthproject.repository.EbUserRepository;
import mdl.proxysthproject.util.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

      private final EbUserRepository userRepository;
      private final JwtUtils jwtUtils;

      public AuthController(EbUserRepository userRepository, JwtUtils jwtUtils) {
            this.userRepository = userRepository;
            this.jwtUtils = jwtUtils;
      }

      @PostMapping("/login")
      public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
            String phone = payload.get("phone");
            if (phone == null || phone.trim().isEmpty()) {
                  throw new IllegalArgumentException("Số điện thoại không được để trống!");
            }

            // Kiểm tra xem SĐT đã đăng ký chưa[cite: 20]
            EbUser user = userRepository.findByPhone(phone)
                  .orElseThrow(() -> new IllegalArgumentException("Số điện thoại này chưa được đăng ký hệ thống!"));

            if (!"ACTIVE".equals(user.getStatus())) {
                  throw new IllegalStateException("Tài khoản của bạn đã bị vô hiệu hóa!");
            }

            // Sinh JWT Token siêu bảo mật
            String token = jwtUtils.generateToken(phone);

            // Trả token về cho Frontend lưu trữ
            return ResponseEntity.ok(Map.of(
                  "accessToken", token,
                  "phone", user.getPhone(),
                  "name", user.getName()
            ));
      }
}