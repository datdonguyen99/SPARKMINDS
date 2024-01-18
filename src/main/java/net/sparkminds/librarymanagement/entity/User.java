package net.sparkminds.librarymanagement.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
    @Size(min = 10, max = 10)
    @Pattern(regexp = "^[0-9]{10}$", message = "Phone number must be 10 digits")
    private String phoneNumber;
}
