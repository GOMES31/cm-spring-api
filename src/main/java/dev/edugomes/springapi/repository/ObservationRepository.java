package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.Observation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ObservationRepository extends JpaRepository<Observation, Long> {
}