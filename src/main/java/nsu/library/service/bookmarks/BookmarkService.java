package nsu.library.service.bookmarks;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.book.BookmarkDTO;
import nsu.library.entity.Book;
import nsu.library.entity.Bookmark;
import nsu.library.entity.BookmarkGroup;
import nsu.library.entity.User;
import nsu.library.repository.BookRepository;
import nsu.library.repository.BookmarkGroupRepository;
import nsu.library.repository.BookmarkRepository;
import nsu.library.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final BookmarkRealTimeService bookmarkRealTimeService;
    private final BookmarkRepository bookmarkRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookmarkGroupRepository bookmarkGroupRepository;

    public List<Bookmark> getUserBookmarksByBook(Long userId, Long bookId) {
        return bookmarkRepository.getBookmarksByUser_idAndBook_Id(userId, bookId);
    }

    public Bookmark getBookmark(Long id) {
        return bookmarkRepository.findById(id).orElseThrow();
    }

    public Bookmark addBookmark(BookmarkDTO dto, Long bookId, Long userId) {
        Bookmark bookmark = new Bookmark();
        Book book = bookRepository.findById(bookId).orElseThrow();
        bookmark.setBook(book);
        User user = userRepository.findById(userId).orElseThrow();
        bookmark.setUser(user);
        bookmark.setGroup(bookmarkGroupRepository.findById(bookmark.getGroup().getId()).orElseThrow());
        if (bookmark.getGroup() != null) {
            bookmarkRealTimeService.handleBookmarkCreated(bookmark);
        }
        bookmark.setSpine_reference(dto.getSpineRef());
        bookmark.setParagraph_index(dto.getParagraphIdx());
        bookmark.setText_bookmark(dto.getText());
        bookmarkRepository.save(bookmark);
        return bookmark;
    }

    public Bookmark AddBookmarkToGroup(Long id, UUID groupID) {
        Bookmark bookmark = bookmarkRepository.findById(id).orElseThrow();
        BookmarkGroup group = bookmarkGroupRepository.findById(groupID).orElseThrow();
        bookmark.setGroup(group);
        bookmarkRealTimeService.handleBookmarkCreated(bookmark);

        return bookmarkRepository.save(bookmark);
    }

    public Bookmark editBookmark(Long id, Long userId, BookmarkDTO dto) {
        Bookmark bookmark = bookmarkRepository.findById(id).orElseThrow();
        if (!Objects.equals(bookmark.getUser().getId(), userId)) {
            throw new AccessDeniedException("You do not have permission to edit this bookmark");
        }
        if (dto.getText() != null) {
            bookmark.setText_bookmark(dto.getText());
        }
        if (dto.getParagraphIdx() != 0) {
            bookmark.setParagraph_index(dto.getParagraphIdx());
        }
        if (dto.getSpineRef() != 0) {
            bookmark.setSpine_reference(dto.getSpineRef());
        }
        if (bookmark.getGroup() != null) {
            bookmarkRealTimeService.handleBookmarkUpdated(bookmark);
        }

        bookmarkRepository.save(bookmark);
        return bookmark;
    }

    public List<Bookmark>  getBookmarksByGroup(UUID groupId) {
        BookmarkGroup group = bookmarkGroupRepository.findById(groupId).orElseThrow(EntityNotFoundException::new);

        return bookmarkRepository.findBookmarksByGroup(group);
    }

    public void deleteBookmark(Long id, Long userId) {
        Bookmark bookmark = bookmarkRepository.findById(id).orElse(null);
        if (bookmark == null) {
            return;
        }
        if (!Objects.equals(bookmark.getUser().getId(), userId)) {
            throw new AccessDeniedException("You do not have permission to delete this bookmark");
        }
        if (bookmark.getGroup() != null) {
            bookmarkRealTimeService.handleBookmarkDeleted(bookmark);
        }

        bookmarkRepository.deleteById(id);
    }
}
