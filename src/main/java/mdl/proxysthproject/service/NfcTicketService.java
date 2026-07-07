package mdl.proxysthproject.service;

import mdl.proxysthproject.model.*;
import mdl.proxysthproject.repository.EbUserRepository;
import mdl.proxysthproject.repository.NfcTicketRepository;
import mdl.proxysthproject.util.NameMaskingUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Optional;

@Service
public class NfcTicketService {

    private final NfcTicketRepository ticketRepository;
    private final EbUserRepository userRepository;
    private final NotificationService notificationService;
    private final LongPollService longPollService;

    @Value("${nfc.max-attempts:3}")
    private int maxAttempts;

    @Value("${nfc.request.ttl-minutes:5}")
    private int requestTtlMinutes;

    @Value("${nfc.helper.ttl-minutes:15}")
    private int helperTtlMinutes;

    public NfcTicketService(NfcTicketRepository ticketRepository, EbUserRepository userRepository,
                            NotificationService notificationService, LongPollService longPollService) {
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.longPollService = longPollService;
    }

    public Optional<EbUser> validateHelperPhone(String phone) {
        return userRepository.findByPhone(phone).filter(u -> "ACTIVE".equals(u.getStatus()));
    }

    public NfcTicket createTicket(String requesterId, String requesterName, String helperPhone, JourneyType journeyType) {
        EbUser helper = validateHelperPhone(helperPhone)
                .orElseThrow(() -> new IllegalArgumentException("Số điện thoại không phù hợp để NFC hộ"));

        NfcTicket ticket = NfcTicket.builder()
                .id(UUID.randomUUID().toString())
                .requesterId(requesterId)
                .requesterName(requesterName)
                .helperPhone(helperPhone)
                .helperName(helper.getName())
                .status(TicketStatus.CREATED)
                .journeyType(journeyType)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusMinutes(requestTtlMinutes))
                .attemptCounts(0)
                .build();
        
        addAudit(ticket, "CREATE", requesterId, "Ticket created");
        ticket.setStatus(TicketStatus.SENT);
        addAudit(ticket, "SEND_OTT", "SYSTEM", "Sent OTT to helper");
        
        ticketRepository.save(ticket);
        
        sendOttToHelper(ticket);

        return ticket;
    }
    
    public void resendOtt(String ticketId) {
        NfcTicket ticket = getValidTicket(ticketId, false);
        sendOttToHelper(ticket);
        addAudit(ticket, "RESEND_OTT", ticket.getRequesterId(), "Resent OTT to helper");
    }

    private void sendOttToHelper(NfcTicket ticket) {
        String title = "Hỗ trợ cập nhật sinh trắc học!";
        String body = "Bạn nhận được yêu cầu hỗ trợ cập nhật sinh trắc học từ " + ticket.getRequesterName();
        notificationService.sendOtt(ticket.getHelperPhone(), title, body);
    }

    public void declineTicket(String ticketId, String helperPhone) {
        NfcTicket ticket = getValidTicket(ticketId, true);
        if (!ticket.getHelperPhone().equals(helperPhone)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        ticket.setStatus(TicketStatus.DECLINED);
        addAudit(ticket, "DECLINE", helperPhone, "Helper declined the request");
        
        // Notify requester via long poll
        NfcResultResponse response = new NfcResultResponse(ticketId, TicketStatus.DECLINED, "Người hỗ trợ đã từ chối yêu cầu", null);
        longPollService.resolve(ticketId, response);
    }
    
    public NfcTicket getValidTicket(String ticketId, boolean forHelper) {
        NfcTicket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticket not found"));
                
        int ttl = forHelper ? helperTtlMinutes : requestTtlMinutes;
        
        if (ticket.getCreatedAt().plusMinutes(ttl).isBefore(LocalDateTime.now())) {
            ticket.setStatus(TicketStatus.EXPIRED);
            addAudit(ticket, "EXPIRE", "SYSTEM", "Ticket expired");
            
            // Notify long poll if waiting
            NfcResultResponse response = new NfcResultResponse(ticketId, TicketStatus.EXPIRED, "Yêu cầu đã hết hạn", null);
            longPollService.resolve(ticketId, response);
            
            throw new IllegalStateException("Ticket has expired");
        }
        
        if (ticket.getStatus() == TicketStatus.DECLINED || ticket.getStatus() == TicketStatus.COMPLETED) {
            throw new IllegalStateException("Ticket is no longer active");
        }
        
        return ticket;
    }

    public void submitNfcScan(String ticketId, String helperPhone, NfcPayload payload) {
        NfcTicket ticket = getValidTicket(ticketId, true);
        if (!ticket.getHelperPhone().equals(helperPhone)) {
            throw new IllegalArgumentException("Unauthorized");
        }
        
        ticket.setAttemptCounts(ticket.getAttemptCounts() + 1);
        
        if (payload.isForceMatchFail()) {
            addAudit(ticket, "SCAN_FAIL", helperPhone, "NFC scan or match failed (Forced)");
            if (ticket.getAttemptCounts() >= maxAttempts) {
                ticket.setStatus(TicketStatus.MATCH_FAILED);
                NfcResultResponse response = new NfcResultResponse(ticketId, TicketStatus.MATCH_FAILED, "NFC hộ thất bại sau nhiều lần thử", null);
                longPollService.resolve(ticketId, response);
                throw new IllegalStateException("NFC hộ thất bại quá số lần cho phép.");
            }
            throw new IllegalStateException("Lỗi quét NFC (Cố ý). Vui lòng thử lại. (Lần " + ticket.getAttemptCounts() + "/" + maxAttempts + ")");
        }
        
        ticket.setNfcPayload(payload);
        ticket.setStatus(TicketStatus.COMPLETED);
        addAudit(ticket, "SCAN_SUCCESS", helperPhone, "NFC scan successful");
        
        // Resolve long poll
        NfcResultResponse response = new NfcResultResponse(ticketId, TicketStatus.COMPLETED, "NFC hộ thành công", payload);
        longPollService.resolve(ticketId, response);
    }
    
    private void addAudit(NfcTicket ticket, String action, String user, String reason) {
        AuditLog log = AuditLog.builder()
                .timestamp(LocalDateTime.now())
                .action(action)
                .user(user)
                .ip("127.0.0.1") // Mock IP
                .reason(reason)
                .build();
        ticket.addAuditLog(log);
    }
}
