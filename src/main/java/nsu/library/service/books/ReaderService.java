package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import nsu.library.service.storage.Storage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class ReaderService {
    private final BookRepository bookRepository;
    private final Storage storage;

    public ReaderService(BookRepository bookRepository, @Qualifier("MinioService") Storage storage) {
        this.bookRepository = bookRepository;
        this.storage = storage;
    }

    public String getBook(Long id) {
        String url = null;
        String linkToBook;
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            linkToBook = "hitman.epub";
        } else {
            linkToBook = book.getLinkToBook();
        }
        return storage.getBook(linkToBook);
    }
}
