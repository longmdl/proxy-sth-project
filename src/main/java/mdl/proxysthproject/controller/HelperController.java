package mdl.proxysthproject.controller;

import mdl.proxysthproject.entity.EbUser;
import mdl.proxysthproject.entity.NfcTicket;
import mdl.proxysthproject.repository.EbUserRepository;
import mdl.proxysthproject.repository.NfcTicketRepository;
import mdl.proxysthproject.service.NfcTicketService;
import mdl.proxysthproject.util.NameMaskingUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelperController {

    private final NfcTicketService ticketService;
    private final NfcTicketRepository ticketRepository;
    private final EbUserRepository userRepository;

    public HelperController(NfcTicketService ticketService, NfcTicketRepository ticketRepository, EbUserRepository userRepository) {
        this.ticketService = ticketService;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/users/signup")
    public ResponseEntity<?> signup(@RequestBody EbUser user) {
        if (userRepository.existsById(user.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại này đã được đăng ký!");
        }
        user.setStatus("ACTIVE"); // default to ACTIVE
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User created successfully", "phone", user.getPhone()));
    }

    @PostMapping("/helper/validate-phone")
    public ResponseEntity<?> validatePhone(@RequestBody Map<String, String> payload) {
        String phone = payload.get("phone");
        EbUser helper = ticketService.validateHelperPhone(phone)
                .orElseThrow(() -> new IllegalArgumentException("Số điện thoại không phù hợp để NFC hộ"));
        
        return ResponseEntity.ok(Map.of(
            "phone", helper.getPhone(),
            "maskedName", NameMaskingUtil.maskName(helper.getName())
        ));
    }

    @GetMapping("/helper/requests")
    public List<NfcTicket> getRequests() {
        String realPhone = SecurityContextHolder.getContext().getAuthentication().getName();
        return ticketRepository.findByHelperPhone(realPhone);
    }
}
