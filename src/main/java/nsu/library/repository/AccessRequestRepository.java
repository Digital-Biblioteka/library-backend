package nsu.library.repository;

import nsu.library.entity.BookAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AccessRequestRepository extends JpaRepository<BookAccessRequest, UUID> {
    List<BookAccessRequest> getAccessRequestsByGroup_Id(UUID groupID);
    List<BookAccessRequest> getAccessRequestsByUser_Id(Long userId);
}
