package net.sparkminds.librarymanagement.config.audit;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.RequiredArgsConstructor;
import net.sparkminds.librarymanagement.entity.Auditable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

@RequiredArgsConstructor
public class AuditingEntityListenerImpl extends AuditingEntityListener {
    private final AuditorAwareImpl auditorAware;

    @PrePersist
    public void onCreate(Object entity) {
        if (entity instanceof Auditable auditable) {
            Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
            if (currentAuditor.isPresent()) {
                auditable.setCreatedBy(currentAuditor.get());
                auditable.setCreatedDate(LocalDateTime.now().toInstant(ZoneOffset.UTC));
            }
        }
    }

    @PreUpdate
    public void onUpdate(Object entity) {
        if (entity instanceof Auditable auditable) {
            Optional<String> currentAuditor = auditorAware.getCurrentAuditor();
            if (currentAuditor.isPresent()) {
                auditable.setLastModifiedBy(currentAuditor.get());
                auditable.setLastModifiedDate(LocalDateTime.now().toInstant(ZoneOffset.UTC));
            }
        }
    }
}