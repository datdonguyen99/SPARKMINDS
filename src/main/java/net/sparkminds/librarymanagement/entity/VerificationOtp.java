package net.sparkminds.librarymanagement.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
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

import java.util.Calendar;
import java.util.Date;

import static net.sparkminds.librarymanagement.utils.AppConstants.EXPIRATION;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "otps")
public class VerificationOtp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "otp", length = 6)
    @Size(min = 6, max = 6)
    private String otp;

    @OneToOne(targetEntity = Account.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "account_id", referencedColumnName = "id")
    private Account account;

    @Column(name = "expire_date")
    @Future
    private Date expiryDate;

    private Date calculateExpiryDate(final int expiryTimeInSeconds) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.SECOND, expiryTimeInSeconds);
        return new Date(cal.getTime().getTime());
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

    public VerificationOtp(final String otp) {
        super();
        this.otp = otp;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
}
