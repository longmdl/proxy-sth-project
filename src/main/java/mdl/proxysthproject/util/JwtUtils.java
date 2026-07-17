package mdl.proxysthproject.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {

      @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
      private String jwtSecret;

      @Value("${jwt.expirationMs:86400000}")
      private int jwtExpirationMs;

      private Key getSigningKey() {
            byte[] keyBytes = io.jsonwebtoken.io.Decoders.BASE64.decode(jwtSecret);
            return Keys.hmacShaKeyFor(keyBytes);
      }

      // 1. Dập Token mới khi User đăng nhập thành công
      public String generateToken(String phone) {
            return Jwts.builder()
                  .setSubject(phone) // Nhét số điện thoại vào trong Token
                  .setIssuedAt(new Date()) // Thời điểm tạo
                  .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs)) // Thời điểm hết hạn
                  .signWith(getSigningKey(), SignatureAlgorithm.HS256) // Đóng dấu bảo mật
                  .compact();
      }

      // 2. Lấy số điện thoại ra từ một Token bất kỳ (để biết ai đang gọi API)
      public String getPhoneFromToken(String token) {
            return Jwts.parserBuilder()
                  .setSigningKey(getSigningKey())
                  .build()
                  .parseClaimsJws(token)
                  .getBody()
                  .getSubject();
      }

      // 3. Lính gác kiểm tra xem Token có hợp lệ, bị giả mạo hay đã hết hạn chưa
      public boolean validateToken(String authToken) {
            try {
                  Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(authToken);
                  return true;
            } catch (JwtException | IllegalArgumentException e) {
                  // Có thể dùng Logger ở đây thay vì System.err cho chuẩn
                  System.err.println("Token không hợp lệ hoặc đã hết hạn: " + e.getMessage());
            }
            return false;
      }
}