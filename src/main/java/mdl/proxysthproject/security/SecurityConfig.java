package mdl.proxysthproject.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

      private final JwtAuthenticationFilter jwtAuthenticationFilter;

      public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
            this.jwtAuthenticationFilter = jwtAuthenticationFilter;
      }

      @Bean
      public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                  // 1. Tắt CSRF (vì chúng ta dùng Stateless API với Token)
                  .csrf(csrf -> csrf.disable())
                  
                  // 2. Không tạo Session lưu trên Server
                  .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                  
                  // 3. Phân quyền Endpoint
                  .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/", "/index.html", "/css/**", "/js/**").permitAll()
                        .requestMatchers("/api/users/signup", "/api/auth/login", "/api/debug/**").permitAll()
                        
                        .requestMatchers("/api/ekyc/**", "/api/nfc/**", "/api/helper/validate-phone").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.POST, "/api/nfc-tickets", "/api/nfc-tickets/{id}/resend").permitAll()
                        .requestMatchers(org.springframework.http.HttpMethod.GET, "/api/nfc-tickets/{id}/await").permitAll()
                        
                        .requestMatchers("/api/helper/requests", "/api/nfc-tickets/{id}/decline", "/api/nfc-tickets/{id}/nfc-scan").authenticated()
                        
                        .anyRequest().authenticated()
                  );

            // 4. Chèn bộ lọc JWT của chúng ta vào trước bộ lọc mặc định của Spring
            http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

            return http.build();
      }
}