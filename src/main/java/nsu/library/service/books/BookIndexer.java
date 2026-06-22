package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsu.library.entity.Book;
import nsu.library.entity.IndexingStatus;
import nsu.library.repository.BookRepository;
import nsu.library.service.search.SearchIndexClient;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;


@Slf4j
@Component
@RequiredArgsConstructor
public class BookIndexer {

    private final BookRepository bookRepository;
    private final SearchIndexClient searchIndexClient;

    @Async("indexingExecutor")
    public void indexBookAsync(Book book) {
        book.setIndexingStatus(IndexingStatus.INDEXING);
        bookRepository.save(book);
        log.info("[BookIndexer] indexing started for book id={}, title='{}'", book.getId(), book.getTitle());
        try {
            searchIndexClient.indexBook(book);
            book.setIndexingStatus(IndexingStatus.INDEXED);
            log.info("[BookIndexer] indexing completed for book id={}", book.getId());
        } catch (Exception e) {
            log.error("[BookIndexer] indexing FAILED for book id={}: {}", book.getId(), e.getMessage());
            book.setIndexingStatus(IndexingStatus.FAILED);
        }
        bookRepository.save(book);
    }
}