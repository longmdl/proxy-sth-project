package mdl.proxysthproject.controller;

import mdl.proxysthproject.dto.NfcResultResponse;
import mdl.proxysthproject.entity.NfcPayload;
import mdl.proxysthproject.entity.NfcTicket;
import mdl.proxysthproject.enums.JourneyType;
import mdl.proxysthproject.service.LongPollService;
import mdl.proxysthproject.service.NfcTicketService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.Map;

@RestController
@RequestMapping("/api/nfc-tickets")
public class NfcTicketController {

    private final NfcTicketService ticketService;
    private final LongPollService longPollService;

    public NfcTicketController(NfcTicketService ticketService, LongPollService longPollService) {
        this.ticketService = ticketService;
        this.longPollService = longPollService;
    }

    @PostMapping
    public ResponseEntity<?> createTicket(@RequestBody Map<String, String> payload) {
        try {
            String requesterId = payload.get("requesterId");
            String requesterName = payload.get("requesterName");
            String helperPhone = payload.get("helperPhone");
            JourneyType journeyType = JourneyType.valueOf(payload.getOrDefault("journeyType", "NTB"));
            
            NfcTicket ticket = ticketService.createTicket(requesterId, requesterName, helperPhone, journeyType);
            return ResponseEntity.ok(ticket);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/await")
    public DeferredResult<NfcResultResponse> awaitResult(@PathVariable String id) {
        // Ensure ticket is still valid before blocking
        ticketService.getValidTicket(id, false);
        return longPollService.createDeferredResult(id);
    }

    @PostMapping("/{id}/resend")
    public ResponseEntity<?> resendOtt(@PathVariable String id) {
        try {
            ticketService.resendOtt(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<?> declineTicket(@PathVariable String id, @RequestBody Map<String, String> payload) {
        String helperPhone = payload.get("helperPhone");
        try {
            ticketService.declineTicket(id, helperPhone);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{id}/nfc-scan")
    public ResponseEntity<?> submitNfcScan(@PathVariable String id, @RequestBody Map<String, Object> payload) {
        String helperPhone = (String) payload.get("helperPhone");
        
        NfcPayload nfcPayload = NfcPayload.builder()
                .idNumber((String) payload.get("idNumber"))
                .fullName((String) payload.get("fullName"))
                .dob((String) payload.get("dob"))
                .expiry((String) payload.get("expiry"))
                .portraitHash((String) payload.get("portraitHash"))
                .forceMatchFail((Boolean) payload.getOrDefault("forceMatchFail", false))
                .build();
                
        try {
            ticketService.submitNfcScan(id, helperPhone, nfcPayload);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
