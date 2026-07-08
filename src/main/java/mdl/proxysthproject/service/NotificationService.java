package mdl.proxysthproject.service;

import lombok.extern.slf4j.Slf4j;
import mdl.proxysthproject.model.OttMessage;
import mdl.proxysthproject.repository.OttMessageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class NotificationService {
    
    private final OttMessageRepository repository;

    public NotificationService(OttMessageRepository repository) {
        this.repository = repository;
    }

    public void sendOtt(String phone, String title, String body) {
        log.info("Sending OTT to {}: {} - {}", phone, title, body);
        OttMessage msg = OttMessage.builder()
                .id(java.util.UUID.randomUUID().toString())
                .recipientPhone(phone)
                .title(title)
                .body(body)
                .timestamp(LocalDateTime.now())
                .build();
        repository.save(msg);
    }

    public List<OttMessage> getMessagesForPhone(String phone) {
        return repository.findByRecipientPhone(phone);
    }
    
    public List<OttMessage> getAllMessages() {
        return repository.findAll();
    }
}
