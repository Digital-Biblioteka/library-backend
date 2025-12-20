package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.list.LastReadBookDTO;
import nsu.library.entity.Book;
import nsu.library.entity.LastRead;
import nsu.library.repository.BookRepository;
import nsu.library.repository.LastReadBooksRepository;
import nsu.library.util.LastReadId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class LastReadService {
    private final LastReadBooksRepository lastReadBooksRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    /**
     * Добавить книжку в список прочитанных.
     *
     * @param bookId ид книжки
     * @param userId ид юзера
     * @return созданный и добавленный в бд объект связи книга-юзер
     */
    public LastRead addBookToLastRead(Long bookId, Long userId) {
        LastRead lastRead = new LastRead();
        lastRead.setUserId(userId);
        lastRead.setBookId(bookId);
        return lastReadBooksRepository.save(lastRead);
    }

    /**
     * Получить список прочитанных книг юзера.
     *
     * @param userId ид юзера
     * @return возвращает список дто с ид юзера и книги, а также дто книжки
     */
    public List<LastReadBookDTO> getLastReadListByUser(Long userId) {
        List<LastRead> readBooks = lastReadBooksRepository.getLastReadByUserId(userId);
        List<LastReadBookDTO> readBooksDTO = new ArrayList<>();
        for (LastRead lastRead : readBooks) {
            Book book = bookRepository.findById(lastRead.getBookId()).orElse(null);
            if (book != null) {
                LastReadBookDTO lastReadBookDTO = new LastReadBookDTO();
                lastReadBookDTO.setUserId(lastRead.getUserId());
                lastReadBookDTO.setBookId(lastRead.getBookId());
                lastReadBookDTO.setBook(bookService.convertBookToDTO(book));
                readBooksDTO.add(lastReadBookDTO);
            }
        }
        return readBooksDTO;
    }

    public void deleteBookFromLastRead(Long bookId, Long userId) {
        LastReadId id = new LastReadId();
        id.setBookId(bookId);
        id.setUserId(userId);
        lastReadBooksRepository.deleteById(id);
    }
}
