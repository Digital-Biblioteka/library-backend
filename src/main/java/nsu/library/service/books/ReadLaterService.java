package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.ReadLaterBookDTO;
import nsu.library.entity.Book;
import nsu.library.entity.ReadLater;
import nsu.library.repository.BookRepository;
import nsu.library.repository.ReadLaterRepository;
import nsu.library.util.ReadLaterId;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadLaterService {
    private final ReadLaterRepository readLaterRepository;
    private final BookRepository bookRepository;
    private final BookService bookService;

    public ReadLater addBookToReadLater(Long userId, Long bookId) {
        ReadLater readLater = new ReadLater();
        readLater.setUserId(userId);
        readLater.setBookId(bookId);
        return readLaterRepository.save(readLater);
    }

    public void deleteBookFromReadLater(Long userId, Long bookId) {
        ReadLaterId id = new ReadLaterId();
        id.setUserId(userId);
        id.setBookId(bookId);
        readLaterRepository.deleteById(id);
    }

    public List<ReadLaterBookDTO> getListOfReadLaterBooksByUser(Long userId) {
        List<ReadLater> readLaterList = readLaterRepository.findByUserId(userId);
        List<ReadLaterBookDTO> laterBookDTOList = new ArrayList<>();
        for (ReadLater readLater : readLaterList) {
            Book book = bookRepository.findById(readLater.getBookId()).orElse(null);
            if (book != null) {
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
