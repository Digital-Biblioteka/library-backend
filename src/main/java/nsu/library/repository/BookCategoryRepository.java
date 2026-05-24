package nsu.library.repository;

import nsu.library.entity.BookCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookCategoryRepository extends JpaRepository<BookCategory, UUID> {
    Optional<BookCategory> findByName(String name);
    boolean existsByName(String name);
}
