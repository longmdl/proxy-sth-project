package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.EkycSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EkycSessionRepository extends JpaRepository<EkycSession, String> {
}
