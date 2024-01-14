package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.User;
import net.sparkminds.librarymanagement.entity.VerificationOtp;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationOtpRepository extends JpaRepository<VerificationOtp, Long> {
    VerificationOtp findByOtp(String otp);

    VerificationOtp findByUser(User user);
}
