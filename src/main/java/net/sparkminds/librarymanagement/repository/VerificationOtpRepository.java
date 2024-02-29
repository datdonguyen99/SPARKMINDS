package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.entity.VerificationOtp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface VerificationOtpRepository extends JpaRepository<VerificationOtp, Long> {
    VerificationOtp findByOtp(String otp);

    @Query("SELECT v FROM VerificationOtp v WHERE v.otp = :otp AND v.account.email = :email")
    Optional<VerificationOtp> findVerificationOtpByOtp(@Param("otp") String otp,
                                                       @Param("email") String email);

    VerificationOtp findByAccount(Account account);
}
