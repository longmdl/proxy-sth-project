package mdl.proxysthproject.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "nfc_tickets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NfcTicket {
    @Id
    private String id;
    private String requesterId;
    private String requesterName;
    private String helperPhone;
    private String helperName;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    
    @Enumerated(EnumType.STRING)
    private JourneyType journeyType;
    
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
    private int attemptCounts;
    
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "nfc_payload_id", referencedColumnName = "id")
    private NfcPayload nfcPayload;
    
    @Builder.Default
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private List<AuditLog> auditLogs = new ArrayList<>();
    
    public void addAuditLog(AuditLog log) {
        this.auditLogs.add(log);
    }
}
