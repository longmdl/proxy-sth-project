package mdl.proxysthproject.controller;

import mdl.proxysthproject.model.AuditLog;
import mdl.proxysthproject.model.NfcTicket;
import mdl.proxysthproject.repository.NfcTicketRepository;
import mdl.proxysthproject.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DebugController {

    private final NotificationService notificationService;
    private final NfcTicketRepository ticketRepository;

    public DebugController(NotificationService notificationService, NfcTicketRepository ticketRepository) {
        this.notificationService = notificationService;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping("/debug/notifications")
    public List<NotificationService.OttMessage> getAllNotifications() {
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
