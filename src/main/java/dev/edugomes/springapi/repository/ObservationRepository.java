package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObservationRepository extends JpaRepository<Observation, Long> {

    @Query("SELECT o FROM Observation o WHERE o.task.id = :taskId ORDER BY o.createdAt DESC")
    List<Observation> findByTaskIdOrderByCreatedAtDesc(@Param("taskId") Long taskId);
}