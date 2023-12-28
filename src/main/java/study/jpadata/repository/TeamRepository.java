package study.jpadata.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.jpadata.entity.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
