package net.sparkminds.librarymanagement.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static net.sparkminds.librarymanagement.utils.AppConstants.EXPIRATION;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "otps")
public class VerificationOtp extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "otp", unique = true, length = 6)
    @Size(min = 6, max = 6, message = "OTP must contain 6 digits")
    @Pattern(regexp = "^\\d{6}$", message = "Invalid OTP format")
    private String otp;

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "account_id")
    private Account account;

    @Column(name = "expire_date")
    @Future
    private Instant expiryDate;

    private Instant calculateExpiryDate(final int expiryTimeInSeconds) {
        return LocalDateTime.now().plus(Duration.ofSeconds(expiryTimeInSeconds)).toInstant(ZoneOffset.UTC);
    }

    public void updateOtp(final String otp) {
        this.otp = otp;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public VerificationOtp(final Account account, final String otp) {
        super();
        this.otp = otp;
        this.account = account;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
}
