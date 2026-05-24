package nsu.library.repository;

import nsu.library.entity.Book;
import nsu.library.entity.BookPermission;
import nsu.library.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface BookPermissionRepository extends JpaRepository<BookPermission, Long> {
    List<BookPermission> getBookPermissionsByUser(User user);
    List<BookPermission> getBookPermissionsByBook(Book book);
    List<BookPermission> findBookPermissionsByBookAndUser(Book book, User user);
    List<BookPermission> getBookPermissionByTimeExpiresBefore(Instant time);
    void deleteBookPermissionByBookIdAndUserId(Long bookId, Long userId);
}
