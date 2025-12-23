package nsu.library.repository;

import nsu.library.entity.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
    List<Bookmark> getBookmarksByUser_Id(Long userId);
    List<Bookmark> getBookmarksByUser_idAndBook_Id(Long userId, Long bookId);
}
