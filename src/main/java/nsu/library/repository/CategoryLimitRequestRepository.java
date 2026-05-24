package nsu.library.repository;

import nsu.library.entity.CategoryLimitRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryLimitRequestRepository extends JpaRepository<CategoryLimitRequest, UUID> {
    List<CategoryLimitRequest> findByGroup_Id(UUID groupId);
    List<CategoryLimitRequest> findByGroup_Librarian_Id(Long librarianId);
}
