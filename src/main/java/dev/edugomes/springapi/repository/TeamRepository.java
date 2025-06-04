package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team,Long> {
}