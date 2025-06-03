package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TeamRepository extends JpaRepository<Team, Long> {

    @Query("SELECT t FROM Team t JOIN t.members tm WHERE tm.user.email = :email")
    List<Team> findTeamsByUserEmail(@Param("email") String email);

    List<Team> findByNameContainingIgnoreCase(String name);
}