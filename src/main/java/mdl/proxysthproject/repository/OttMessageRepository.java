package mdl.proxysthproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mdl.proxysthproject.entity.OttMessage;

import java.util.List;


public interface OttMessageRepository extends JpaRepository<OttMessage, String> {
    List<OttMessage> findByRecipientPhone(String recipientPhone);
}
