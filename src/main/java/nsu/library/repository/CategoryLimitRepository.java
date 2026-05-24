package nsu.library.repository;

import nsu.library.entity.CategoryLimit;
import nsu.library.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryLimitRepository extends JpaRepository<CategoryLimit, Long> {
    Optional<CategoryLimit> findByGroup_IdAndCategory_Id(UUID groupId, UUID categoryId);
    List<CategoryLimit> findByGroup(Group group);
}
