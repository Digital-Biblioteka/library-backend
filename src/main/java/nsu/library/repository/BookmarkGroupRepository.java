package nsu.library.repository;

import nsu.library.entity.BookmarkGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookmarkGroupRepository extends JpaRepository<BookmarkGroup, UUID> {
    List<BookmarkGroup> findBookmarkGroupsByName(String name);
    BookmarkGroup findBookmarkGroupsByAccessToken(UUID accessToken);
}
