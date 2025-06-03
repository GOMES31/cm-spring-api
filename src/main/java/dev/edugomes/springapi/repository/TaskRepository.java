package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.ProjectTask;
import dev.edugomes.springapi.domain.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface TaskRepository extends JpaRepository<ProjectTask, Long> {

    List<ProjectTask> findByProjectId(Long projectId);

    List<ProjectTask> findByStatus(Status status);

    @Query("SELECT t FROM ProjectTask t WHERE t.project.id = :projectId ORDER BY t.createdAt DESC")
    List<ProjectTask> findByProjectIdOrderByCreatedAtDesc(@Param("projectId") Long projectId);

    @Query("SELECT t FROM ProjectTask t JOIN t.assignees a WHERE a.user.email = :email ORDER BY t.createdAt DESC")
    List<ProjectTask> findTasksAssignedToUser(@Param("email") String email);

    @Query("SELECT t FROM ProjectTask t JOIN t.project p JOIN p.team tm JOIN tm.members m WHERE m.user.email = :email ORDER BY t.createdAt DESC")
    List<ProjectTask> findTasksForUserTeams(@Param("email") String email);

    @Query("SELECT t FROM ProjectTask t WHERE t.endDate < :currentDate AND t.status != 'FINISHED'")
    List<ProjectTask> findOverdueTasks(@Param("currentDate") Date currentDate);

    @Query("SELECT t FROM ProjectTask t WHERE t.endDate BETWEEN :startDate AND :endDate")
    List<ProjectTask> findTasksDueBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);

    @Query("SELECT COUNT(t) FROM ProjectTask t WHERE t.project.id = :projectId")
    Long countTasksByProject(@Param("projectId") Long projectId);

    @Query("SELECT COUNT(t) FROM ProjectTask t WHERE t.project.id = :projectId AND t.status = :status")
    Long countTasksByProjectAndStatus(@Param("projectId") Long projectId, @Param("status") Status status);

    @Query("SELECT t FROM ProjectTask t JOIN t.project p JOIN p.team tm JOIN tm.members m WHERE m.user.email = :email AND t.project.id = :projectId")
    List<ProjectTask> findTasksByProjectAndUserAccess(@Param("projectId") Long projectId, @Param("email") String email);
}