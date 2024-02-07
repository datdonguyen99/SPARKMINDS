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
@Table(name = "tokens")
public class VerificationToken extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token", unique = true, length = 50)
    @Size(min = 1, max = 50)
    private String token;

    @ManyToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "account_id")
    private Account account;

    @Column(name = "expire_date")
    @Future
    private Instant expiryDate;

    private Instant calculateExpiryDate(final int expiryTimeInSeconds) {
        return LocalDateTime.now().plus(Duration.ofSeconds(expiryTimeInSeconds)).toInstant(ZoneOffset.UTC);
    }

    public void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    public VerificationToken(final Account account, final String token) {
        super();
        this.token = token;
        this.account = account;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
}
