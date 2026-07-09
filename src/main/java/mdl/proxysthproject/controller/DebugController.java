package mdl.proxysthproject.controller;

import mdl.proxysthproject.entity.AuditLog;
import mdl.proxysthproject.entity.NfcTicket;
import mdl.proxysthproject.entity.OttMessage;
import mdl.proxysthproject.repository.NfcTicketRepository;
import mdl.proxysthproject.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import mdl.proxysthproject.repository.EbUserRepository;
import mdl.proxysthproject.repository.EkycSessionRepository;
import mdl.proxysthproject.repository.OttMessageRepository;
import org.springframework.web.bind.annotation.DeleteMapping;

@RestController
@RequestMapping("/api")
public class DebugController {

    private final NotificationService notificationService;
    private final NfcTicketRepository ticketRepository;
    private final EbUserRepository ebUserRepository;
    private final EkycSessionRepository ekycSessionRepository;
    private final OttMessageRepository ottMessageRepository;

    public DebugController(
        NotificationService notificationService, 
        NfcTicketRepository ticketRepository,
        EbUserRepository ebUserRepository,
        EkycSessionRepository ekycSessionRepository,
        OttMessageRepository ottMessageRepository) 
    {
        this.notificationService = notificationService;
        this.ticketRepository = ticketRepository;
        this.ebUserRepository = ebUserRepository;
        this.ekycSessionRepository = ekycSessionRepository;
        this.ottMessageRepository = ottMessageRepository;
    }

    @DeleteMapping("/debug/clear")
    public ResponseEntity<?> clearAllData() {
        ticketRepository.deleteAll();
        ebUserRepository.deleteAll();
        ekycSessionRepository.deleteAll();
        ottMessageRepository.deleteAll();
        return ResponseEntity.ok(java.util.Map.of("message", "All data cleared successfully"));
    }

    @GetMapping("/debug/notifications")
    public List<OttMessage> getAllNotifications() {
        return notificationService.getAllMessages();
    }

    @GetMapping("/nfc-tickets/{id}/audit")
    public ResponseEntity<List<AuditLog>> getTicketAudit(@PathVariable String id) {
        return ticketRepository.findById(id)
                .map(NfcTicket::getAuditLogs)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
