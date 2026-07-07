# Prototype: Tính năng Thu thập Sinh trắc học Hộ (NFC Proxy)

Đây là bản prototype (Spring Boot) mô phỏng tính năng "Thu thập sinh trắc học hộ" của HDBank. Tính năng này cho phép một khách hàng có thiết bị hỗ trợ NFC (Người hỗ trợ) giúp một khách hàng khác không có NFC (Người cần hỗ trợ) quét chip CCCD của họ. 

Dữ liệu NFC sau khi quét sẽ được chuyển ngược về quy trình của người cần hỗ trợ thông qua cơ chế **Long-Polling**.

## Kiến trúc & Cách hoạt động

1. **Giao tiếp Long-Polling**:
   - Khi Người cần hỗ trợ gửi yêu cầu "NFC hộ", client của họ sẽ gọi API `GET /api/nfc-tickets/{id}/await`.
   - Backend sử dụng `DeferredResult` của Spring để "treo" (hold) request này lại mà không làm nghẽn luồng xử lý (thread).
   - Khi Người hỗ trợ dùng thiết bị của họ quét thẻ thành công và submit lên server (`POST /api/nfc-tickets/{id}/nfc-scan`), backend sẽ tìm lại `DeferredResult` đang bị treo và trả kết quả về.
   - Nhờ đó, màn hình của Người cần hỗ trợ sẽ lập tức nhận được dữ liệu NFC ngay khi Người hỗ trợ hoàn tất, tạo trải nghiệm liền mạch theo thời gian thực.

2. **Xử lý các luồng thiết bị**:
   - **Giao diện Giả lập**: Giao diện (UI) cung cấp các tuỳ chọn để giả lập thiết bị (Thiết bị tốt, Thiết bị không có NFC, Thiết bị chập chờn).
   - **Thiết bị chập chờn (Shoddy NFC)**: Nếu chọn thiết bị chập chờn, backend sẽ lưu lại số lần quét lỗi vào trong session (đối với Người cần hỗ trợ) hoặc ticket (đối với Người hỗ trợ). Nếu vượt quá số lần cho phép (config `nfc.max-attempts`, mặc định = 3), backend sẽ trả về lỗi `400 Bad Request` với message báo hết số lần thử, và thay đổi trạng thái sang `MATCH_FAILED` hoặc tự động điều hướng sang đăng ký NFC hộ.
   
3. **Mô hình Dữ liệu (In-Memory)**:
   - Các bảng dữ liệu như `EbUser` (Người dùng EB), `EkycSession` (Phiên eKYC), và `NfcTicket` (Phiên yêu cầu hỗ trợ) được lưu trực tiếp trên RAM bằng `ConcurrentHashMap` để phục vụ việc demo nhanh chóng.

## Hướng dẫn Chạy ứng dụng

Yêu cầu hệ thống:
- Java 17+
- Maven

1. Khởi động server bằng Maven:
   ```bash
   ./mvnw spring-boot:run
   ```
2. Mở trình duyệt và truy cập vào:
   ```
   http://localhost:8080/index.html
   ```

## Hướng dẫn Kiểm thử (Demo luồng NTB)

Màn hình được chia làm 2 nửa: Trái (Requester - Người cần hỗ trợ) và Phải (Helper - Người hỗ trợ).

1. **Người cần hỗ trợ**: Nhấn `1. Bắt đầu eKYC`.
2. **Người cần hỗ trợ**: Ở mục thiết bị quét NFC, chọn **Không có NFC** (hoặc chọn Thiết bị chập chờn và click quét 3 lần). Sau đó nhấn `2. Quét NFC (Self)`. Màn hình sẽ yêu cầu đăng ký NFC Hộ.
3. **Người cần hỗ trợ**: Nhập số điện thoại Người hỗ trợ (VD: `0901234567`) và nhấn `Kiểm tra SĐT`.
4. **Người cần hỗ trợ**: Tích chọn "Tôi đồng ý T&C" và nhấn `Gửi yêu cầu & Chờ kết quả`.
5. **Người hỗ trợ**: Nhấn `Check Inbox (Nhận OTT)`.
6. **Người hỗ trợ**: Một yêu cầu sẽ hiện ra. Nhấn `Mở yêu cầu`.
7. **Người hỗ trợ**: Lựa chọn loại thiết bị của bạn. Nếu thiết bị lỗi (Shoddy), khi nhấn hoàn thành bạn sẽ thấy báo lỗi và có thể thử lại cho tới khi hết lượt. Chọn "Thiết bị tốt" và nhấn `Hoàn thành Scan & Gửi`.
8. **Kết quả**: Yêu cầu bên phía Người cần hỗ trợ sẽ lập tức hoàn thành và hiển thị dữ liệu thành công!

## Dữ liệu Mock / Giả lập
- Số điện thoại Helper hợp lệ: `0901234567`, `0912345678`.
- Thông tin CCCD, Dữ liệu sinh trắc học và OTT messages đều được fake hoàn toàn bằng text tĩnh để tập trung vào logic kết nối luồng nghiệp vụ.
