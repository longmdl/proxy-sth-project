package mdl.proxysthproject.service;

import mdl.proxysthproject.model.EkycSession;
import mdl.proxysthproject.repository.EkycSessionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
public class EkycService {
    private final EkycSessionRepository repository;

    @org.springframework.beans.factory.annotation.Value("${nfc.max-attempts:3}")
    private int maxAttempts;

    public EkycService(EkycSessionRepository repository) {
        this.repository = repository;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public java.util.Optional<EkycSession> getSession(String transactionId) {
        return repository.findById(transactionId);
    }

    public EkycSession startSession() {
        EkycSession session = EkycSession.builder()
                .transactionId(UUID.randomUUID().toString())
                .idNumber("079090123456")
                .birthDate("01/01/1990")
                .expiration(LocalDate.now().plusYears(10).toString())
                .channel("MOBILE")
                .partnerId("HDB_MOBILE")
                .accessToken("dummy-access-token")
                .xApiKey("dummy-api-key")
                .secretSignature("dummy-secret")
                .algSignature("HS256")
                .build();
        repository.save(session);
        return session;
    }
}
