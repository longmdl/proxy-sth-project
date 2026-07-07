package mdl.proxysthproject.controller;

import mdl.proxysthproject.model.EbUser;
import mdl.proxysthproject.model.NfcTicket;
import mdl.proxysthproject.repository.NfcTicketRepository;
import mdl.proxysthproject.service.NfcTicketService;
import mdl.proxysthproject.util.NameMaskingUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/helper")
public class HelperController {

    private final NfcTicketService ticketService;
    private final NfcTicketRepository ticketRepository;

    public HelperController(NfcTicketService ticketService, NfcTicketRepository ticketRepository) {
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
    }

    @PostMapping("/validate-phone")
    public ResponseEntity<?> validatePhone(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phone");
        try {
            EbUser helper = ticketService.validateHelperPhone(phone)
                    .orElseThrow(() -> new IllegalArgumentException("Số điện thoại không phù hợp để NFC hộ"));
            
            return ResponseEntity.ok(Map.of(
                "phone", helper.getPhone(),
                "maskedName", NameMaskingUtil.maskName(helper.getName())
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/requests/{phone}")
    public List<NfcTicket> getRequests(@PathVariable String phone) {
        return ticketRepository.findByHelperPhone(phone);
    }
}
