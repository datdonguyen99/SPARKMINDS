package net.sparkminds.librarymanagement.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.FetchType;
import lombok.Getter;
import lombok.Setter;
import net.sparkminds.librarymanagement.utils.RoleName;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role_name", nullable = false)
    private RoleName roleName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private List<Account> accountList = new ArrayList<>();
}
