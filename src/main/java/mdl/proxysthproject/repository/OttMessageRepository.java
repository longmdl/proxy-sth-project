package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.OttMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OttMessageRepository extends JpaRepository<OttMessage, String> {
    List<OttMessage> findByRecipientPhone(String recipientPhone);
}
