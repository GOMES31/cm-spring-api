package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.Observation;
import dev.edugomes.springapi.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT o FROM Task t JOIN t.observations o WHERE t.id = :taskId")
    List<Observation> findObservationsByTaskId(@Param("taskId") Long taskId);
}