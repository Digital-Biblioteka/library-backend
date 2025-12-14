package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookWrapper;
import nsu.library.dto.TocItemDTO;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import nsu.library.service.minio.MinioService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReaderService {
    private final BookRepository bookRepository;
    private final MinioService minioService;
    private final BookImport bookImport;
    private final ReaderCacheService readerCacheService;

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


    public List<TocItemDTO> getTableOfContents(Long bookId){
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
        return bookImport.GetTableOfContents(realBook);
    }

    public byte[] getHtmlChapterByTocItem(Long bookId, TocItemDTO tocItemDTO) {
        BookWrapper bookWrapper = readerCacheService.getBookWrapper(bookId);
        byte[] html = null;
        try {
            html = bookImport.getHtmlFromSpine(bookWrapper, tocItemDTO);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }
}
