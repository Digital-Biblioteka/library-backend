package nsu.library.repository;

import nsu.library.entity.BookLimitRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookLimitRequestRepository extends JpaRepository<BookLimitRequest, UUID> {
    List<BookLimitRequest> findByGroup_Id(UUID groupId);
    List<BookLimitRequest> findByGroup_Librarian_Id(Long librarianId);
}
