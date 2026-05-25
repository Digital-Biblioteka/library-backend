package nsu.library.repository;

import nsu.library.entity.BookmarkGroupUser;
import nsu.library.util.BookmarkGroupUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookmarkGroupUserRepository  extends JpaRepository<BookmarkGroupUser, BookmarkGroupUserId> {
}
