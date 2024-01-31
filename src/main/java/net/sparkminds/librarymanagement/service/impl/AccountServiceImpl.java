package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.entity.CustomAccount;
import net.sparkminds.librarymanagement.repository.AccountRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements UserDetailsService {
    private final AccountRepository accountRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) {
        Account account = accountRepository.findByEmail(email);
        if (account == null) {
            throw new UsernameNotFoundException("User Not Found in database with email: " + email);
        }

        return CustomAccount.build(account);
    }
}
