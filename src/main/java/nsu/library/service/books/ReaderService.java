package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nl.siegmann.epublib.domain.SpineReference;
import nsu.library.dto.BookWrapper;
import nsu.library.dto.ChapterDTO;
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

    public ChapterDTO getHtmlChapterByTocItem(Long bookId, TocItemDTO tocItemDTO) {
        BookWrapper bookWrapper = readerCacheService.getBookWrapper(bookId);
        byte[] html = null;
        SpineReference ref;
        try {
            ref = bookImport.getSpineFromToc(bookWrapper, tocItemDTO);
            html = ref.getResource().getData();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        ChapterDTO dto = new ChapterDTO();
        dto.setHtml(html);
        int idx = bookWrapper.getSpines().indexOf(ref);
        dto.setSpineIdx(idx);
        if (idx > 0) {
            dto.setHasPrev(true);
        }
        int totalLen = bookWrapper.getSpines().size();
        if (idx < (totalLen - 1)) {
            dto.setHasNext(true);
        }
        dto.setTotalSpines(totalLen);
        return dto;
    }

    public ChapterDTO getHtmlChapterBySpineIdx(Long bookId, Integer spineIdx) {
        BookWrapper bookWrapper = readerCacheService.getBookWrapper(bookId);
        System.out.println(bookWrapper.getSpines());
        byte[] html = null;
        try {
            SpineReference spine = bookImport.getSpineByIdx(bookWrapper.getSpines(), spineIdx);
            if (spine == null) {
                return null;
            }
            html = spine.getResource().getData();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        ChapterDTO dto = new ChapterDTO();
        dto.setHtml(html);
        if (spineIdx > 0) {
            dto.setHasPrev(true);
        }
        dto.setSpineIdx(spineIdx);
        int totalLen = bookWrapper.getSpines().size();
        if (spineIdx < (totalLen - 1)) {
            dto.setHasNext(true);
        }
        dto.setTotalSpines(totalLen);
        System.out.println(dto.getTotalSpines());
        return dto;
    }
}
