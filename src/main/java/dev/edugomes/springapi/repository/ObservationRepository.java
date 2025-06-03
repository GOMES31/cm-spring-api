package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ObservationRepository extends JpaRepository<Observation, Long> {

    List<Observation> findByTaskId(Long taskId);

    List<Observation> findByUserId(Long userId);

    @Query("SELECT o FROM Observation o WHERE o.task.id = :taskId ORDER BY o.createdAt DESC")
    List<Observation> findByTaskIdOrderByCreatedAtDesc(@Param("taskId") Long taskId);

    @Query("SELECT o FROM Observation o JOIN o.task t JOIN t.project p JOIN p.team tm JOIN tm.members m WHERE m.user.email = :email ORDER BY o.createdAt DESC")
    List<Observation> findObservationsByUserEmail(@Param("email") String email);
}
