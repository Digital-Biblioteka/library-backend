package nsu.library.repository;

import nsu.library.entity.ReadingPosition;
import nsu.library.util.ReadingPositionId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadingPositionRepository extends JpaRepository<ReadingPosition, ReadingPositionId> {
}
