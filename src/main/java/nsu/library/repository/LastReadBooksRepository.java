package nsu.library.repository;

import nsu.library.entity.LastRead;
import nsu.library.util.LastReadId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LastReadBooksRepository extends JpaRepository<LastRead, LastReadId> {
    List<LastRead> getLastReadByUserId(Long userId);
}
