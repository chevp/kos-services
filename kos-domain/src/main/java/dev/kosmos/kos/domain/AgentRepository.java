package dev.kosmos.kos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AgentRepository extends JpaRepository<Agent, String> {

    List<Agent> findAllByOrderByUpdatedAtDesc();
}
