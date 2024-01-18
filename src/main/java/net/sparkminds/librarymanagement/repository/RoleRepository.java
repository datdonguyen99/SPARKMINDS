package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.Role;
import net.sparkminds.librarymanagement.utils.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(RoleName name);
}
