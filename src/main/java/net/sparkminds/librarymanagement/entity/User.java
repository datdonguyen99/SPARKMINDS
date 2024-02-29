package net.sparkminds.librarymanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import jakarta.persistence.CascadeType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@SuperBuilder
@Entity
@Table(name = "users")
public class User extends Account {
    @Column(name = "first_name", length = 50)
    @Size(min = 1, max = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    @Size(min = 1, max = 50)
    private String lastName;

    @Column(name = "address", length = 50)
    @Size(min = 1, max = 50)
    private String address;

    @Column(name = "phone_number", length = 10)
    @Size(min = 10, max = 10, message = "Phone number must be 10 digits")
    @Pattern(regexp = "^(\\+84|0)(3[2-9]|5[689]|7[06-9]|8[1-9]|9\\d)\\d{7}$", message = "Invalid phone number")
    private String phoneNumber;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans = new ArrayList<>();

    @NotNull
    @Column(name = "is_available")
    @Builder.Default
    private Boolean isAvailable = true;
}
