package net.sparkminds.librarymanagement.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import net.sparkminds.librarymanagement.utils.Status;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "accounts", uniqueConstraints = {@UniqueConstraint(columnNames = {"email"})})
@Inheritance(strategy = InheritanceType.JOINED)
public class Account {
    private static final long serialVersionUID = 1L;

    /**
     * id is primary key of table accounts
     * Use database auto-increment mechanism to create a value for PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username", length = 50)
    @Size(min = 1, max = 50)
    private String username;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9_!#$%&’*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$", message = "Invalid email format")
    @Column(name = "email", unique = true, length = 254)
    @Size(min = 5, max = 254)
    private String email;

    @JsonIgnore
    @NotNull
    @Column(name = "password_hash", nullable = false, length = 60)
    @Size(min = 6, max = 60)
    private String password;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @ManyToOne(targetEntity = Role.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "role_id", referencedColumnName = "id")
    private Role role;

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VerificationToken> verificationToken = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VerificationOtp> verificationOtp = new ArrayList<>();

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    private List<Session> sessions = new ArrayList<>();

    @Column(name = "secret_key", length = 50)
    private String secretKey;

    @Column(name = "enable_mfa")
    @Builder.Default
    private boolean enableMFA = false;

    @Column(name = "latest_login_time")
    private LocalDateTime latestLoginTime;

    @NotNull
    @Column(name = "login_count")
    @Builder.Default
    private int loginCount = 0;
}
