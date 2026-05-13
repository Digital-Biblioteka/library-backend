package nsu.library.service.bookpermissions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import nsu.library.entity.BookPermission;
import nsu.library.entity.User;
import nsu.library.repository.BookLimitRepository;
import nsu.library.repository.BookPermissionRepository;
import nsu.library.repository.BookRepository;
import nsu.library.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class AccessControlService {
    private final BookLimitRepository bookLimitRepository;
    private final UserRepository userRepository;
    private final BookPermissionRepository bookPermissionRepository;
    private final BookRepository bookRepository;

    public List<BookPermission> ListBookPermissionsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }

        return bookPermissionRepository.getBookPermissionsByUser(user);
    }

    public List<BookPermission> ListBookPermissionsByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new EntityNotFoundException("Book with id " + bookId + " not found");
        }

        return bookPermissionRepository.getBookPermissionsByBook(book);
    }

    public boolean CheckPermissionToBook(Long bookID, Long userID) {
        Book book = bookRepository.findById(bookID).orElseThrow(() -> new EntityNotFoundException("Book with id " + bookID + " not found"));
        User user = userRepository.findById(userID).orElseThrow(() -> new EntityNotFoundException("User with id " + userID + " not found"));

        List<BookPermission> bookPermission = bookPermissionRepository.findBookPermissionsByBookAndUser(book, user);
        return !bookPermission.isEmpty();
    }
}
