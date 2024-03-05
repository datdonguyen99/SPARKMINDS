package net.sparkminds.librarymanagement.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.payload.response.ConfigResponse;
import net.sparkminds.librarymanagement.service.ConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
public class ConfigController {
    private final Logger logger = LoggerFactory.getLogger(ConfigController.class);

    private final ConfigService configService;

    @Operation(summary = "Get all current configs", description = "Get all current configs", tags = {"Config function"})
    @GetMapping("/config")
    public ResponseEntity<List<ConfigResponse>> getCurrentConfig() {
        logger.debug("REST request to get all current configs");
        return new ResponseEntity<>(configService.getCurrentConfig(), HttpStatus.OK);
    }
}
