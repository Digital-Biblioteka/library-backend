package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookDTO;
import nsu.library.dto.BookPreviewDTO;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import nsu.library.service.minio.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

@Service
@RequiredArgsConstructor
public class ReaderService {
    private final BookService bookService;
    private final BookRepository bookRepository;
    private final MinioService minioService;

    /**
     * Извлекаем превью из книжки.
     * cover - ссылка на обложку в минио, metadata- данные книжки из бд
     *
     * @param bookId ид книжки
     * @return dto
     */
    public BookPreviewDTO getBookPreview(@PathVariable Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        BookDTO metadata = bookService.convertBookToDTO(book);
        String bookLink = book.getLinkToBook();
        String coverLink = minioService.getBookCover(bookLink);

        BookPreviewDTO bookPreviewDTO = new BookPreviewDTO();
        bookPreviewDTO.setCover(coverLink);
        bookPreviewDTO.setMetadata(metadata);
        return bookPreviewDTO;
    }
}
