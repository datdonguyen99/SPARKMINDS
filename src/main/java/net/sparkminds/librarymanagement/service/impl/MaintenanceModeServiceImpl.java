package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Account;
import net.sparkminds.librarymanagement.entity.Config;
import net.sparkminds.librarymanagement.repository.AccountRepository;
import net.sparkminds.librarymanagement.service.ConfigService;
import net.sparkminds.librarymanagement.service.MailSenderService;
import net.sparkminds.librarymanagement.service.MaintenanceModeService;
import net.sparkminds.librarymanagement.utils.RoleName;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static net.sparkminds.librarymanagement.utils.AppConstants.MAINTENANCE_MODE_KEY;

@Service
@RequiredArgsConstructor
public class MaintenanceModeServiceImpl implements MaintenanceModeService {
    private final RedisTemplate<Object, Object> redisTemplate;

    private final AccountRepository accountRepository;

    private final ConfigService configService;

    private final MailSenderService mailSenderService;

    @Override
    @Transactional
    public void setMode(boolean enabled) {
        // save KEY: MAINTENANCE_MODE & VALUE: enabled into redis
        redisTemplate.opsForValue().set(MAINTENANCE_MODE_KEY, enabled);

        Config redisConfig = Config.builder()
                .key(MAINTENANCE_MODE_KEY)
                .value(String.valueOf(enabled))
                .build();
        configService.save(redisConfig);

        if (enabled) {
            List<String> emails = accountRepository.findAll().stream()
                    .filter(account -> account.getRole().getRoleName().equals(RoleName.ROLE_USER))
                    .map(Account::getEmail)
                    .toList();

            mailSenderService.sendEmailMaintenanceToAllAccount(emails);
        }
    }

    @Override
    public boolean isEnable() {
        // Get VALUE: enabled from KEY: MAINTENANCE_MODE
        Boolean isEnabled = (Boolean) redisTemplate.opsForValue().get(MAINTENANCE_MODE_KEY);
        if (isEnabled == null) {
            return false;
        }
        return isEnabled;
    }
}
