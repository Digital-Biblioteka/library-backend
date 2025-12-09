package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ReaderService {
    private final BookRepository bookRepository;

    public String getBook(Long id) {
        String url = null;
        String linkToBook;
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            linkToBook = "hitman.epub";
        } else {
            linkToBook = book.getLinkToBook();
        }
    }
}
