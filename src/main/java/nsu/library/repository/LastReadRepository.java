package nsu.library.repository;

import nsu.library.entity.LastRead;
import nsu.library.util.LastReadId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LastReadRepository extends JpaRepository<LastRead, LastReadId> {
    List<LastRead> getLastReadByUser_Id(Long userId);
}
