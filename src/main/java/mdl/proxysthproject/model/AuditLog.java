package mdl.proxysthproject.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AuditLog {
    private LocalDateTime timestamp;
    private String action;
    private String user;
    private String ip;
    private String reason;
}
