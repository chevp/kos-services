package dev.kosmos.kos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, String> {

    List<Project> findAllByOrderByUpdatedAtDesc();

    boolean existsByNamespaceAndName(String namespace, String name);
}
