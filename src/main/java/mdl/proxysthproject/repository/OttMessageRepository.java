package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.OttMessage;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;


public interface OttMessageRepository extends JpaRepository<OttMessage, String> {
    List<OttMessage> findByRecipientPhone(String recipientPhone);
}
