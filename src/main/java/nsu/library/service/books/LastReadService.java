package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import nsu.library.entity.LastRead;
import nsu.library.entity.User;
import nsu.library.repository.BookRepository;
import nsu.library.repository.LastReadRepository;
import nsu.library.repository.UserRepository;
import nsu.library.util.LastReadId;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class LastReadService {
    private final LastReadRepository lastReadRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public LastRead addBookToLastRead(Long bookid, Long userId) {
        LastRead lastRead = new LastRead();
        User user = userRepository.findById(userId).orElseThrow();
        Book book = bookRepository.findById(bookid).orElseThrow();
        lastRead.setUser(user);
        lastRead.setBook(book);
        return lastReadRepository.save(lastRead);
    }

    public List<LastRead> getLastReadListByUser(Long userId) {
        return lastReadRepository.getLastReadByUser_Id(userId);
    }

    public LastRead deleteBookFromLastRead(Long bookId, Long userId) {
        LastReadId id = new LastReadId();
        id.setBookId(bookId);
        id.setUserId(userId);
        LastRead lastRead = lastReadRepository.findById(id).orElseThrow();

        lastReadRepository.deleteById(id);

        return lastRead;
    }
}
