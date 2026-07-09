package mdl.proxysthproject.exception;

import mdl.proxysthproject.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice 
public class GlobalExceptionHandler {

      /**
       * 1. Xử lý các lỗi về Trạng thái nghiệp vụ (IllegalStateException)
       * Thường xảy ra ở Service: Hết hạn ticket, Spam gửi lại mã...
       */
      @ExceptionHandler(IllegalStateException.class)
      public ResponseEntity<ErrorResponse> handleIllegalStateException(IllegalStateException ex) {
            String message = ex.getMessage();
            HttpStatus status = HttpStatus.BAD_REQUEST; // Mặc định là 400
            String code = "BUSINESS_ERROR";

            // Tự động phân tích message để ép về chuẩn Rate Limit nếu người dùng spam gửi OTT
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
       * 2. Xử lý các lỗi về Tham số không hợp lệ (IllegalArgumentException)
       * Ví dụ: Không tìm thấy Ticket, Sai số điện thoại, Unauthorized...
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
       * 3. Xử lý lỗi Validation (MethodArgumentNotValidException)
       * Khi dùng @Valid ở Controller mà Frontend truyền thiếu field hoặc sai định dạng email/phone
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
                  .details(errors) // Trả về chi tiết lỗi của từng ô input cho frontend map vào UI
                  .build();

            return new ResponseEntity<>(errorResponse, status);
      }

      /**
       * 4. Lớp bọc cuối cùng (Fallback) - Xử lý tất cả các lỗi hệ thống không lường trước được (Exception)
       * Giúp hệ thống không bao giờ bị "văng" lỗi raw (Stacktrace) nguy hiểm ra ngoài
       */
      @ExceptionHandler(Exception.class)
      public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
            // Log lỗi chi tiết ở phía Server để bạn debug
            ex.printStackTrace(); 

            HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR; // HTTP 500
            ErrorResponse errorResponse = ErrorResponse.builder()
                  .timestamp(LocalDateTime.now())
                  .status(status.value())
                  .error(status.getReasonPhrase())
                  .message("Hệ thống đang gặp sự cố gián đoạn, vui lòng thử lại sau!") // Giấu thông tin kỹ thuật nhạy cảm
                  .code("INTERNAL_SERVER_ERROR")
                  .build();

            return new ResponseEntity<>(errorResponse, status);
      }
}