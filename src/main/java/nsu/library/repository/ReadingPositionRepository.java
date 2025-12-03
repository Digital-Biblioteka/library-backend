package nsu.library.repository;

import nsu.library.entity.ReadingPosition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingPositionRepository extends JpaRepository<Long, ReadingPosition> {
}
