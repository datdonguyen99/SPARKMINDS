package net.sparkminds.librarymanagement.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.service.MaintenanceModeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/v1/admin")
@RequiredArgsConstructor
public class MaintenanceModeController {
    private final Logger logger = LoggerFactory.getLogger(MaintenanceModeController.class);

    private final MaintenanceModeService maintenanceModeService;

    @Operation(summary = "Enable maintenance mode", description = "Enable maintenance mode", tags = {"Maintenance mode functions"})
    @PostMapping("/enable-maintenance")
    public ResponseEntity<Void> enableMaintenance() {
        logger.debug("REST request to enable maintenance mode");
        maintenanceModeService.setMode(true);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }

    @Operation(summary = "Disable maintenance mode", description = "Disable maintenance mode", tags = {"Maintenance mode functions"})
    @PostMapping("/disable-maintenance")
    public ResponseEntity<Void> disableMaintenance() {
        logger.debug("REST request to disable maintenance mode");
        maintenanceModeService.setMode(false);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
