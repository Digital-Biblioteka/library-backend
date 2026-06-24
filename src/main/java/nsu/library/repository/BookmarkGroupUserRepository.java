package nsu.library.repository;

import nsu.library.entity.BookmarkGroup;
import nsu.library.entity.BookmarkGroupUser;
import nsu.library.util.BookmarkGroupUserId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BookmarkGroupUserRepository  extends JpaRepository<BookmarkGroupUser, BookmarkGroupUserId> {
    List<BookmarkGroupUser> findBookmarkGroupUsersByGroup(BookmarkGroup bookmarkGroup);
    List<BookmarkGroupUser> findBookmarkGroupUserByUser_IdAndGroup_Book_Id(Long userId, Long bookID);
}
