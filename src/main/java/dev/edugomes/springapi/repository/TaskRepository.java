package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.ProjectTask;
import dev.edugomes.springapi.domain.Status;
import dev.edugomes.springapi.domain.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<ProjectTask, Long> {

    @Query("SELECT o FROM ProjectTask t JOIN t.observations o WHERE t.id = :taskId")
    List<Observation> findObservationsByTaskId(@Param("taskId") Long taskId);
}