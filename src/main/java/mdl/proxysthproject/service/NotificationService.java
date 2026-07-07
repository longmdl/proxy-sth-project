package mdl.proxysthproject.service;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NotificationService {
    
    @Data
    @Builder
    public static class OttMessage {
        private String id;
        private String recipientPhone;
        private String title;
        private String body;
        private LocalDateTime timestamp;
    }

    private final List<OttMessage> messages = new ArrayList<>();

    public void sendOtt(String phone, String title, String body) {
        log.info("Sending OTT to {}: {} - {}", phone, title, body);
        OttMessage msg = OttMessage.builder()
                .id(java.util.UUID.randomUUID().toString())
                .recipientPhone(phone)
                .title(title)
                .body(body)
                .timestamp(LocalDateTime.now())
                .build();
        messages.add(msg);
    }

    public List<OttMessage> getMessagesForPhone(String phone) {
        return messages.stream()
                .filter(m -> m.getRecipientPhone().equals(phone))
                .collect(Collectors.toList());
    }
    
    public List<OttMessage> getAllMessages() {
        return new ArrayList<>(messages);
    }
}
