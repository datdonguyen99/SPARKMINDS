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
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import net.sparkminds.librarymanagement.utils.Status;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "accounts", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email"})
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Account {
    /**
     * id is primary key of table accounts
     * Use database auto-increment mechanism to create a value for PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", length = 50)
    @Size(min = 1, max = 50)
    private String name;

    // email required notnull and unique to use authorize
    @Email
    @NotNull
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

    @ManyToOne(targetEntity = Role.class, fetch = FetchType.LAZY)
    @JoinColumn(nullable = false, name = "role_id", referencedColumnName = "id")
    private Role role;
}