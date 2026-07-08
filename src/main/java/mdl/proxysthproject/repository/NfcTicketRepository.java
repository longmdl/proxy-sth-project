package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.NfcTicket;
import mdl.proxysthproject.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface NfcTicketRepository extends JpaRepository<NfcTicket, String> {
    List<NfcTicket> findByHelperPhone(String helperPhone);
    long countByRequesterIdAndStatusIn(String requesterId, List<TicketStatus> statuses);
}
