package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("SELECT s FROM Session s WHERE s.jti = :jti AND s.isActive = true ORDER BY s.expireDate DESC LIMIT 1")
    Optional<Session> findLatestActiveSessionByJti(@Param("jti") String jti);

    Optional<Session> findByJti(String jti);
}