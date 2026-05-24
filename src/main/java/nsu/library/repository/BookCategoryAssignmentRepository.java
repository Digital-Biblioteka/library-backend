package nsu.library.repository;

import nsu.library.entity.BookCategoryAssignment;
import nsu.library.util.BookCategoryId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookCategoryAssignmentRepository extends JpaRepository<BookCategoryAssignment, BookCategoryId> {
    List<BookCategoryAssignment> findByCategory_Id(UUID categoryId);
    List<BookCategoryAssignment> findByBook_Id(Long bookId);
    boolean existsByBook_IdAndCategory_Id(Long bookId, UUID categoryId);
}
