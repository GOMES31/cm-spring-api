package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.TeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamMemberRepository extends JpaRepository<TeamMember, Long> {
}