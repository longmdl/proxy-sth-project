package mdl.proxysthproject.exception;

import mdl.proxysthproject.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

// THÊM CÁC THƯ VIỆN SECURITY
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.access.AccessDeniedException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice 
public class GlobalExceptionHandler {

      /**
       * 1. XỬ LÝ LỖI SECURITY: Sai Token, Token hết hạn, Đăng nhập xịt (HTTP 401)
       */
      @ExceptionHandler(AuthenticationException.class)
      public ResponseEntity<ErrorResponse> handleAuthenticationException(AuthenticationException ex) {
            HttpStatus status = HttpStatus.UNAUTHORIZED;
            ErrorResponse errorResponse = ErrorResponse.builder()
                  .timestamp(LocalDateTime.now())
                  .status(status.value())
                  .error(status.getReasonPhrase())
                  .message(ex.getMessage() != null ? ex.getMessage() : "Yêu cầu đăng nhập hoặc Token không hợp lệ!")
                  .code("UNAUTHORIZED_ACCESS")
                  .build();
            return new ResponseEntity<>(errorResponse, status);
      }

      /**
       * 2. XỬ LÝ LỖI SECURITY: Đăng nhập rồi nhưng không có quyền vào endpoint này (HTTP 403)
       */
      @ExceptionHandler(AccessDeniedException.class)
      public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException ex) {
            HttpStatus status = HttpStatus.FORBIDDEN;
            ErrorResponse errorResponse = ErrorResponse.builder()
                  .timestamp(LocalDateTime.now())
                  .status(status.value())
                  .error(status.getReasonPhrase())
                  .message("Bạn không có quyền thực hiện hành động này!")
                  .code("FORBIDDEN_ACCESS")
                  .build();
            return new ResponseEntity<>(errorResponse, status);
      }

      /**
       * 3. Xử lý các lỗi về Trạng thái nghiệp vụ (IllegalStateException)
       */
      @ExceptionHandler(IllegalStateException.class)
      public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
            String message = ex.getMessage(); 
            HttpStatus status = HttpStatus.BAD_REQUEST; // Mặc định là 400
            String code = "BUSINESS_ERROR"; 

            if (message != null && (message.contains("Vui lòng chờ") || message.contains("vượt quá số lần"))) { 
                  status = HttpStatus.TOO_MANY_REQUESTS; // HTTP 429 chuẩn Production
                  code = "RATE_LIMIT_EXCEEDED"; 
            }

            ErrorResponse errorResponse = ErrorResponse.builder()
                  .timestamp(LocalDateTime.now())
                  .status(status.value())
                  .error(status.getReasonPhrase())
                  .message(message)
                  .code(code)
                  .build();

            return new ResponseEntity<>(errorResponse, status);
      }

      /**
       * 4. Xử lý các lỗi về Tham số không hợp lệ (IllegalArgumentException)
       */
      @ExceptionHandler(IllegalArgumentException.class)
      public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException ex) {
            HttpStatus status = HttpStatus.BAD_REQUEST; 
            String code = "INVALID_ARGUMENT"; 

            if ("Unauthorized".equalsIgnoreCase(ex.getMessage())) { 
                  status = HttpStatus.UNAUTHORIZED; // HTTP 401 nếu không có quyền can thiệp vào ticket
                  code = "UNAUTHORIZED_ACCESS"; 
            }

            ErrorResponse errorResponse = ErrorResponse.builder()
                  .timestamp(LocalDateTime.now())
                  .status(status.value())
                  .error(status.getReasonPhrase())
                  .message(ex.getMessage())
                  .code(code)
                  .build();

            return new ResponseEntity<>(errorResponse, status);
      }

      /**
       * 5. Xử lý lỗi Validation (MethodArgumentNotValidException)
       */
      @ExceptionHandler(MethodArgumentNotValidException.class)
      public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>(); 
            ex.getBindingResult().getAllErrors().forEach((error) -> { 
                  String fieldName = ((FieldError) error).getField(); 
                  String errorMessage = error.getDefaultMessage(); 
                  errors.put(fieldName, errorMessage); 
            }); 

            HttpStatus status = HttpStatus.BAD_REQUEST; 
            ErrorResponse errorResponse = ErrorResponse.builder()
                  .timestamp(LocalDateTime.now())
                  .status(status.value())
                  .error(status.getReasonPhrase())
                  .message("Dữ liệu đầu vào không hợp lệ!") 
                  .code("VALIDATION_FAILED") 
                  .details(errors) 
                  .build();

            return new ResponseEntity<>(errorResponse, status);
      }

      /**
       * 6. Lớp bọc cuối cùng (Fallback)
       */
      @ExceptionHandler(Exception.class)
      public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
            ex.printStackTrace(); 
            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; 
            ErrorResponse errorResponse = ErrorResponse.builder()
                  .timestamp(LocalDateTime.now())
                  .status(status.value())
                  .error(status.getReasonPhrase())
                  .message("Hệ thống đang gặp sự cố gián đoạn, vui lòng thử lại sau!") 
                  .code("INTERNAL_SERVER_ERROR") 
                  .build();

            return new ResponseEntity<>(errorResponse, status);
      }
}