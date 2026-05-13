package nsu.library.repository;

import nsu.library.entity.Book;
import nsu.library.entity.BookLimit;
import nsu.library.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookLimitRepository extends JpaRepository<BookLimit, Long> {
    BookLimit findByBookAndGroup(Book book, Group group);
}
