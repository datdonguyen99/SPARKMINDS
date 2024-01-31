package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("SELECT s FROM Session s WHERE s.jti = :jti AND s.isActive = true ORDER BY s.expireDate DESC")
    List<Session> findActiveSessionByJti(@Param("jti") String jti);

    default Optional<Session> findLatestActiveSessionByJti(String jti) {
        List<Session> sessions = findActiveSessionByJti(jti);
        return sessions.isEmpty() ? Optional.empty() : Optional.of(sessions.get(0));
    }

    Session findByJti(String jti);
}