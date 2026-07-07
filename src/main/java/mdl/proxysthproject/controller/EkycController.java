package mdl.proxysthproject.controller;

import mdl.proxysthproject.model.EkycSession;
import mdl.proxysthproject.service.EkycService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class EkycController {

    private final EkycService ekycService;

    public EkycController(EkycService ekycService) {
        this.ekycService = ekycService;
    }

    @PostMapping("/ekyc/start")
    public EkycSession startEkyc() {
        return ekycService.startSession();
    }

    @PostMapping("/nfc/attempt")
    public Map<String, Object> attemptNfc(@RequestBody Map<String, Object> payload) {
        String transactionId = (String) payload.get("transactionId");
        boolean deviceSupported = (Boolean) payload.getOrDefault("deviceSupported", true);
        boolean simulateSuccess = (Boolean) payload.getOrDefault("simulateSuccess", true);
        
        if (!deviceSupported) {
            return Map.of("success", false, "fallbackToHelper", true, "message", "Thiết bị không hỗ trợ NFC");
        }

        EkycSession session = null;
        if (transactionId != null) {
            session = ekycService.getSession(transactionId).orElse(null);
        }

        if (session != null) {
            if (simulateSuccess) {
                return Map.of("success", true, "fallbackToHelper", false, "message", "Quét NFC thành công");
            } else {
                session.setNfcAttemptCount(session.getNfcAttemptCount() + 1);
                int maxAttempts = ekycService.getMaxAttempts();
                
                if (session.getNfcAttemptCount() >= maxAttempts) {
                    return Map.of("success", false, "fallbackToHelper", true, "message", "Vượt quá số lần thử NFC. Chuyển sang NFC hộ.");
                } else {
                    return Map.of("success", false, "fallbackToHelper", false, "message", "Quét thất bại. Vui lòng thử lại. (Lần " + session.getNfcAttemptCount() + "/" + maxAttempts + ")");
                }
            }
        }
        
        return Map.of("success", simulateSuccess, "fallbackToHelper", !simulateSuccess);
    }
}
