package nsu.library.repository;

import nsu.library.entity.CategoryAccessRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CategoryAccessRequestRepository extends JpaRepository<CategoryAccessRequest, UUID> {
    List<CategoryAccessRequest> findByGroup_Id(UUID groupId);
    List<CategoryAccessRequest> findByUser_Id(Long userId);
    boolean existsByUser_IdAndGroup_IdAndCategory_IdAndStatus(
            Long userId, UUID groupId, UUID categoryId,
            CategoryAccessRequest.RequestStatus status);
}
