package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.domain.Team;
import dev.edugomes.springapi.domain.Task;
import dev.edugomes.springapi.domain.Project;
import dev.edugomes.springapi.domain.Observation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT tm.team FROM TeamMember tm WHERE tm.user.email = :email")
    List<Team> findTeamsByUserEmail(@Param("email") String email);

    @Query("SELECT DISTINCT t FROM Task t JOIN t.assignees tm WHERE tm.user.email = :email")
    List<Task> findTasksByUserEmail(@Param("email") String email);

    @Query("SELECT DISTINCT p FROM Project p JOIN p.team.members tm WHERE tm.user.email = :email")
    List<Project> findProjectsByUserEmail(@Param("email") String email);

    @Query("SELECT o FROM Observation o WHERE o.user.email = :email")
    List<Observation> findObservationsByUserEmail(@Param("email") String email);
}