package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.NfcTicket;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Repository
public class NfcTicketRepository {
    private final Map<String, NfcTicket> tickets = new ConcurrentHashMap<>();

    public void save(NfcTicket ticket) {
        tickets.put(ticket.getId(), ticket);
    }

    public Optional<NfcTicket> findById(String id) {
        return Optional.ofNullable(tickets.get(id));
    }

    public List<NfcTicket> findByHelperPhone(String helperPhone) {
        return tickets.values().stream()
                .filter(t -> helperPhone.equals(t.getHelperPhone()))
                .collect(Collectors.toList());
    }
}
