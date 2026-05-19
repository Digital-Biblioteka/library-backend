package nsu.library.repository;

import nsu.library.entity.BookAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessRequestRepository extends JpaRepository<BookAccessRequest, String> {
    List<BookAccessRequest> getAccessRequestsByGroup_Id(String groupID);
    List<BookAccessRequest> getAccessRequestsByUser_Id(Long userId);
}
