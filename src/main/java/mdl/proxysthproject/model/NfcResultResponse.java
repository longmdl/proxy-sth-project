package mdl.proxysthproject.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NfcResultResponse {
    private String ticketId;
    private TicketStatus status;
    private String message;
    private NfcPayload payload; // May be null if failed
}
