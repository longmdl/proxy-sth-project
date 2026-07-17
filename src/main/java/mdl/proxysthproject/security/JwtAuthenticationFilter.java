package mdl.proxysthproject.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mdl.proxysthproject.repository.EbUserRepository;
import mdl.proxysthproject.util.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

      private final JwtUtils jwtUtils;
      private final EbUserRepository userRepository;

      public JwtAuthenticationFilter(JwtUtils jwtUtils, EbUserRepository userRepository) {
            this.jwtUtils = jwtUtils;
            this.userRepository = userRepository;
      }

      @Override
      protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response, 
            FilterChain filterChain)
                  throws ServletException, IOException 
      {
            
            String jwt = parseJwt(request);
            
            if (jwt != null && jwtUtils.validateToken(jwt)) {
                  String phone = jwtUtils.getPhoneFromToken(jwt);
                  
                  // Xác minh số điện thoại trong token có thực sự tồn tại trong Database không
                  userRepository.findByPhone(phone).ifPresent(user -> {
                        // Tạo đối tượng Authentication đã xác thực (mặc định không cần password vì JWT đã bảo mật)
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                                    phone, null, Collections.emptyList());
                        
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        
                        // Set định danh vào Context của Spring Security
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                  });
            }

            filterChain.doFilter(request, response);
      }

      private String parseJwt(HttpServletRequest request) {
            String headerAuth = request.getHeader("Authorization");
            if (headerAuth != null && headerAuth.startsWith("Bearer ")) {
                  return headerAuth.substring(7);
            }
            return null;
      }
}