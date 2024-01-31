package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    Optional<VerificationToken> findVerificationTokenByToken(String token);

    VerificationToken findByAccount(Account account);
}
