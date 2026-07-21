package dev.kosmos.kos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ConnectorConfigRepository extends JpaRepository<ConnectorConfig, String> {

    List<ConnectorConfig> findAllByOrderByUpdatedAtDesc();

    List<ConnectorConfig> findAllByTypeOrderByUpdatedAtDesc(String type);
}
