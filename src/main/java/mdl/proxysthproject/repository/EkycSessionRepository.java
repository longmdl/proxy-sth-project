package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.EkycSession;
import org.springframework.data.jpa.repository.JpaRepository;



public interface EkycSessionRepository extends JpaRepository<EkycSession, String> {
}
