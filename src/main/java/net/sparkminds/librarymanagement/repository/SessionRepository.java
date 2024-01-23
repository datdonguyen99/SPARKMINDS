package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    Optional<Session> findByJti(String jti);
}
