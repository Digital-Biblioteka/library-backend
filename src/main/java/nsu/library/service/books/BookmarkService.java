package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.book.BookmarkDTO;
import nsu.library.entity.Book;
import nsu.library.entity.Bookmark;
import nsu.library.entity.User;
import nsu.library.repository.BookRepository;
import nsu.library.repository.BookmarkRepository;
import nsu.library.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Service
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

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
        bookmark.setSpine_reference(dto.getSpineRef());
        bookmark.setParagraph_index(dto.getParagraphIdx());
        bookmark.setText_bookmark(dto.getText());
        bookmarkRepository.save(bookmark);
        return bookmark;
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
        bookmarkRepository.save(bookmark);
        return bookmark;
    }

    public void deleteBookmark(Long id, Long userId) {
        Bookmark bookmark = bookmarkRepository.findById(id).orElse(null);
        if (bookmark == null) {
            return;
        }
        if (!Objects.equals(bookmark.getUser().getId(), userId)) {
            throw new AccessDeniedException("You do not have permission to delete this bookmark");
        }
        bookmarkRepository.deleteById(id);
    }
}
