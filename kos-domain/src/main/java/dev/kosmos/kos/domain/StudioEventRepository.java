package dev.kosmos.kos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudioEventRepository extends JpaRepository<StudioEvent, String> {

    List<StudioEvent> findAllByOrderByOccurredAtDesc();

    List<StudioEvent> findAllByObjectIdOrderByOccurredAtDesc(String objectId);
}
