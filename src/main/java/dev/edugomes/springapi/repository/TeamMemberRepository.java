package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.TeamMember;
import dev.edugomes.springapi.domain.TeamRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {

    List<TeamMember> findByTeamId(Long teamId);

    List<TeamMember> findByUserId(Long userId);

    List<TeamMember> findByRole(TeamRole role);

    @Query("SELECT tm FROM TeamMember tm WHERE tm.user.email = :email AND tm.team.id = :teamId")
    Optional<TeamMember> findByUserEmailAndTeamId(@Param("email") String email, @Param("teamId") Long teamId);

    @Query("SELECT tm FROM TeamMember tm WHERE tm.user.email = :email")
    List<TeamMember> findByUserEmail(@Param("email") String email);
}