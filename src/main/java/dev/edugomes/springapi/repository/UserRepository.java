package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.User;
import dev.edugomes.springapi.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT tm.team FROM TeamMember tm WHERE tm.user.email = :email")
    List<Team> findTeamsByUserEmail(@Param("email") String email);
}

