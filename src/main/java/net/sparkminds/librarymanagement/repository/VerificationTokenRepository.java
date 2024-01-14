package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.entity.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    VerificationToken findByToken(String token);

    VerificationToken findByUser(User user);
}
