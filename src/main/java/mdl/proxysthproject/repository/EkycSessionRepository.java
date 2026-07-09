package mdl.proxysthproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mdl.proxysthproject.entity.EkycSession;



public interface EkycSessionRepository extends JpaRepository<EkycSession, String> {
}
