package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.list.ReadLaterBookDTO;
import nsu.library.entity.Book;
import nsu.library.entity.ReadLater;
import nsu.library.repository.BookRepository;
import nsu.library.repository.ReadLaterBooksRepository;
import nsu.library.util.ReadLaterId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadLaterService {
    private final ReadLaterBooksRepository readLaterBooksRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    public ReadLater addBookToReadLater(Long userId, Long bookId) {
        ReadLater readLater = new ReadLater();
        readLater.setUserId(userId);
        readLater.setBookId(bookId);
        return readLaterBooksRepository.save(readLater);
    }

    public void deleteBookFromReadLater(Long userId, Long bookId) {
        ReadLaterId id = new ReadLaterId();
        id.setUserId(userId);
        id.setBookId(bookId);
        readLaterBooksRepository.deleteById(id);
    }

    public List<ReadLaterBookDTO> getListOfReadLaterBooksByUser(Long userId) {
        List<ReadLater> readLaterList = readLaterBooksRepository.findByUserId(userId);
        List<ReadLaterBookDTO> laterBookDTOList = new ArrayList<>();
        System.out.println(readLaterList.size());
        System.out.println("user: " + userId);
        for (ReadLater readLater : readLaterList) {
            System.out.println("bookId" + readLater.getBookId());
            Book book = bookRepository.findById(readLater.getBookId()).orElse(null);
            if (book != null) {
                System.out.println(readLater.getBookId());
                ReadLaterBookDTO readLaterBookDTO = new ReadLaterBookDTO();
                readLaterBookDTO.setBookId(book.getId());
                readLaterBookDTO.setUserId(readLater.getUserId());
                readLaterBookDTO.setBook(bookService.convertBookToDTO(book));
                laterBookDTOList.add(readLaterBookDTO);
            }
        }
        return laterBookDTOList;
    }
}
