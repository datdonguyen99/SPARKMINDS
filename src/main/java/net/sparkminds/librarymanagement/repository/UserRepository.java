package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByEmail(String email);

    @Query("SELECT u FROM User u JOIN VerificationToken vToken ON u.id = vToken.id AND vToken.token = :code")
//    @Query(value = "SELECT u.* FROM users u JOIN verification_token vToken ON u.id = vToken.user_id AND vToken.token = :code", nativeQuery = true)
//    @Query(value = "SELECT u.* FROM users u, verification_token vToken WHERE u.id = vToken.user_id AND vToken.token = :code",nativeQuery = true)
    User findByVerificationToken(String code);

    @Query("SELECT u FROM User u JOIN VerificationOtp vOtp ON u.id = vOtp.id AND vOtp.otp = :otp")
    User findByVerificationOtp(String otp);
}
