package mdl.proxysthproject.repository;

import mdl.proxysthproject.entity.NfcTicket;
import mdl.proxysthproject.enums.TicketStatus;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

public interface NfcTicketRepository extends JpaRepository<NfcTicket, String> {
    List<NfcTicket> findByHelperPhone(String helperPhone);
    long countByRequesterIdAndStatusIn(String requesterId, List<TicketStatus> statuses);
    long countByRequesterIdAndCreatedAtAfter(String requesterId, LocalDateTime time);
    Optional<NfcTicket> findFirstByRequesterIdOrderByCreatedAtDesc(String requesterId);
}
