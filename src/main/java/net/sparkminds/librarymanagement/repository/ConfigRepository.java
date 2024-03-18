package net.sparkminds.librarymanagement.repository;

import net.sparkminds.librarymanagement.entity.Config;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfigRepository extends JpaRepository<Config, Long> {
    Optional<Config> findByKey(String key);
}
