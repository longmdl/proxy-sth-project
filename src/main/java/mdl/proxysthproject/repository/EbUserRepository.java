package mdl.proxysthproject.repository;

import mdl.proxysthproject.model.EbUser;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;


public interface EbUserRepository extends JpaRepository<EbUser, String> {
    Optional<EbUser> findByPhone(String phone);
}
