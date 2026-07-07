package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.EkycSession;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EkycSessionRepository {
    private final Map<String, EkycSession> sessions = new ConcurrentHashMap<>();

    public void save(EkycSession session) {
        sessions.put(session.getTransactionId(), session);
    }

    public Optional<EkycSession> findById(String transactionId) {
        return Optional.ofNullable(sessions.get(transactionId));
    }
}
