package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.EbUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EbUserRepository extends JpaRepository<EbUser, String> {
    Optional<EbUser> findByPhone(String phone);
}
