package by.nikiforova.epic_auth_service.repository;

import by.nikiforova.epic_auth_service.entity.Credential;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredentialRepository extends JpaRepository<Credential, Long> {
    Optional<Credential> findByLogin(String login);
    boolean existsByLogin(String login);
    boolean existsByUserId(Long userId);
}
