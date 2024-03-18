package net.sparkminds.librarymanagement.payload.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import net.sparkminds.librarymanagement.entity.Config;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class ConfigResponse {
    private String key;

    private String value;

    public static ConfigResponse buildConfigResponse(Config config) {
        return ConfigResponse.builder()
                .key(config.getKey())
                .value(config.getValue())
                .build();
    }
}
