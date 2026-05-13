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
    List<BookPermission> book(Book book);
    public List<BookPermission> getBookPermissionsByUser(User user);
    public List<BookPermission> getBookPermissionsByBook(Book book);
    public List<BookPermission> getBookPermissionByTimeExpiresBefore(Instant time);
    public void deleteBookPermissionByBookIdAndUserId(Long bookId, Long userId);
}
