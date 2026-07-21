package dev.kosmos.kos.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudioObjectRepository extends JpaRepository<StudioObject, String> {

    List<StudioObject> findAllByOrderByUpdatedAtDesc();

    List<StudioObject> findAllByTypeOrderByUpdatedAtDesc(String type);
}
