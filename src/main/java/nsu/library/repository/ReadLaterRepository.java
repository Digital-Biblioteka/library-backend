package nsu.library.repository;

import nsu.library.util.ReadLaterId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReadLaterRepository extends JpaRepository<ReadLater, ReadLaterId> {
}
