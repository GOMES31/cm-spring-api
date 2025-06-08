package dev.edugomes.springapi.repository;

import dev.edugomes.springapi.domain.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogRepository extends JpaRepository<Log, Long> {

}
