package mdl.proxysthproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import mdl.proxysthproject.enums.TicketStatus;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NfcResultResponse {
    private String ticketId;
    private TicketStatus status;
    private String message;

    private NfcPayloadDto payload; // May be null if failed
}
