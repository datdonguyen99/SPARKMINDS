package net.sparkminds.librarymanagement.service.impl;

import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Config;
import net.sparkminds.librarymanagement.payload.response.ConfigResponse;
import net.sparkminds.librarymanagement.repository.ConfigRepository;
import net.sparkminds.librarymanagement.service.ConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConfigServiceImpl implements ConfigService {
    private final ConfigRepository configRepository;

    @Override
    public void save(Config config) {
        Config updatedConfigs = configRepository.findByKey(config.getKey())
                .map(cfg -> {
                    config.setId(cfg.getId());
                    return config;
                })
                .orElse(config);

        configRepository.save(updatedConfigs);
    }

    @Override
    public List<ConfigResponse> getCurrentConfig() {
        return configRepository.findAll().stream()
                .map(ConfigResponse::buildConfigResponse)
                .toList();
    }
}
