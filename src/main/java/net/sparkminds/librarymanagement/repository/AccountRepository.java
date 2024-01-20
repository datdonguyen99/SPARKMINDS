package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByEmail(String email);
}
