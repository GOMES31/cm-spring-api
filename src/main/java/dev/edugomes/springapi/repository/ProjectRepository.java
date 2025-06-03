package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Long> {

    List<Project> findByTeamId(Long teamId);

    @Query("SELECT p FROM Project p JOIN p.team t JOIN t.members m WHERE m.user.email = :email ORDER BY p.startDate DESC")
    List<Project> findProjectsByUserEmail(@Param("email") String email);

    @Query("SELECT p FROM Project p WHERE p.team.id = :teamId ORDER BY p.startDate DESC")
    List<Project> findByTeamIdOrderByStartDateDesc(@Param("teamId") Long teamId);

    @Query("SELECT p FROM Project p WHERE p.status = :status")
    List<Project> findByStatus(@Param("status") dev.edugomes.springapi.domain.Status status);

    @Query("SELECT p FROM Project p JOIN p.team t JOIN t.members m WHERE m.user.email = :email AND p.status = :status ORDER BY p.startDate DESC")
    List<Project> findProjectsByUserEmailAndStatus(@Param("email") String email, @Param("status") dev.edugomes.springapi.domain.Status status);
}