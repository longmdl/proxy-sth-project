package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.NfcTicket;
import mdl.proxysthproject.model.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NfcTicketRepository extends JpaRepository<NfcTicket, String> {
    List<NfcTicket> findByHelperPhone(String helperPhone);
    long countByRequesterIdAndStatusIn(String requesterId, List<TicketStatus> statuses);
}
