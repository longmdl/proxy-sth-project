package mdl.proxysthproject.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class NfcTicket {
    private String id;
    private String requesterId;
    private String requesterName;
    private String helperPhone;
    private String helperName;
    private TicketStatus status;
    private JourneyType journeyType;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private int attemptCounts;
    private NfcPayload nfcPayload;
    
    @Builder.Default
    private List<AuditLog> auditLogs = new ArrayList<>();
    
    public void addAuditLog(AuditLog log) {
        this.auditLogs.add(log);
    }
}
