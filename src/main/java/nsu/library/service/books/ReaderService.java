package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import nsu.library.service.minio.MinioService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReaderService {
    private final BookRepository bookRepository;
    private final MinioService minioService;
    private final BookImport bookImport;

    /**
     * Извлекаем превью из книжки.
     * cover - ссылка на обложку в минио, metadata- данные книжки из бд
     *
     * @param bookId ид книжки
     * @return dto
     */
    public String getBookPreview(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        String bookLink = book.getLinkToBook();
        return minioService.getBookCover(bookLink);
    }


}
