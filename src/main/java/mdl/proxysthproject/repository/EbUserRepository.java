package mdl.proxysthproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mdl.proxysthproject.entity.EbUser;

import java.util.Optional;


public interface EbUserRepository extends JpaRepository<EbUser, String> {
    Optional<EbUser> findByPhone(String phone);
}
