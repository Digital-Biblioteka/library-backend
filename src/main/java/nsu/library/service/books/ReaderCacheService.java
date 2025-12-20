package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.reader.BookWrapper;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import nsu.library.service.minio.MinioService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class ReaderCacheService {
    private final BookRepository bookRepository;
    private final MinioService minioService;
    private final BookImport bookImport;

    @Cacheable("Bookwrapper")
    public BookWrapper getBookWrapper(Long bookId) {
        Book book = bookRepository.findById(bookId).orElseThrow();
        String bookLink = book.getLinkToBook();
        InputStream bookStream = minioService.getRealBook(bookLink);
        nl.siegmann.epublib.domain.Book realBook = null;
        try {
            realBook = bookImport.readEpubFromStream(bookStream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bookImport.CreateBookWrapperFromBook(realBook);
    }
}
