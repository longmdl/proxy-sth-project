package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.EbUser;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EbUserRepository {
    private final Map<String, EbUser> users = new ConcurrentHashMap<>();

    @PostConstruct
    public void seedData() {
        // Dummy data for testing
        users.put("0901234567", new EbUser("0901234567", "Nguyen Van A", "ACTIVE"));
        users.put("0912345678", new EbUser("0912345678", "Tran Thi B", "ACTIVE"));
        users.put("0923456789", new EbUser("0923456789", "Le C", "INACTIVE")); // Inactive user
    }

    public Optional<EbUser> findByPhone(String phone) {
        return Optional.ofNullable(users.get(phone));
    }
}
