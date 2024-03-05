package net.sparkminds.librarymanagement.service;

import net.sparkminds.librarymanagement.entity.Config;
import net.sparkminds.librarymanagement.payload.response.ConfigResponse;

import java.util.List;

public interface ConfigService {
    void save(Config config);

    List<ConfigResponse> getCurrentConfig();
}
