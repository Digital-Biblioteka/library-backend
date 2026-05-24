package nsu.library.repository;

import nsu.library.entity.Book;
import nsu.library.entity.BookLimit;
import nsu.library.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookLimitRepository extends JpaRepository<BookLimit, Long> {
    BookLimit findByBookAndGroup(Book book, Group group);
    BookLimit findByBook_IdAndGroup_Id(Long bookId, UUID groupId);
    List<BookLimit> findBookLimitsByGroup(Group group);
}
