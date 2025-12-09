package nsu.library.repository;

import nsu.library.util.ReadLaterId;
import org.springframework.data.jpa.repository.JpaRepository;
import nsu.library.entity.ReadLater;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReadLaterBooksRepository extends JpaRepository<ReadLater, ReadLaterId> {
     List<ReadLater> findByUserId(Long userId);
}
