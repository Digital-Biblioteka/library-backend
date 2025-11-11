package nsu.library.service;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookDoc;
import nsu.library.dto.BookDTO;
import nsu.library.dto.SearchQuery;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// add toc to book attributes?
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookImport bookImport;
    private final SearchService searchService;

    public Book addBookManually(BookDTO bookDTO) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setDescription(bookDTO.getDescription());
        book.setIsbn(bookDTO.getIsbn());
        book.setPublisher(bookDTO.getPublisher());
        book.setGenres(bookDTO.getGenre());
        book.setLinkToBook(bookDTO.getLinkToBook());
        bookRepository.save(book);
        return book;
    }

    public Book createBookFromDTO(BookDTO dto) {
        Book book = new Book();
        book.setTitle(dto.getTitle());
        book.setAuthor(dto.getAuthor());
        book.setDescription(dto.getDescription());
        book.setIsbn(dto.getIsbn());
        book.setPublisher(dto.getPublisher());
        book.setGenres(dto.getGenre());
        book.setLinkToBook(dto.getLinkToBook());
        return book;
    }
    public Book addBookAuto(String fileLink) {
        BookDTO book;
        try {
            book = bookImport.parseEpub(bookImport.readEpub(fileLink), fileLink);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Book ourBook = createBookFromDTO(book);
        bookRepository.save(ourBook);
        return ourBook;
    }

    public List<Book> searchBooks(SearchQuery searchQuery) {
        List<BookDoc> books = searchService.searchBooks(searchQuery);
        List<Book> searchBooks = new ArrayList<>();
        for (BookDoc bookDoc : books) {
            searchBooks.add(bookRepository.findByIsbn(bookDoc.isbn()));
        }
        return searchBooks;
    }

    public void deleteBook(String isbn) {
        bookRepository.delete(bookRepository.findByIsbn(isbn));
    }

    public Book editBook(String isbn, BookDTO bookDTO) {
        Book book = bookRepository.findByIsbn(isbn);
        if (bookDTO.getTitle() != null) {
            book.setTitle(bookDTO.getTitle());
        }
        if (bookDTO.getAuthor() != null) {
            book.setAuthor(bookDTO.getAuthor());
        }
        if (bookDTO.getDescription() != null) {
            book.setDescription(bookDTO.getDescription());
        }
        if (bookDTO.getIsbn() != null) {
            book.setIsbn(bookDTO.getIsbn());
        }
        if (bookDTO.getGenre() != null) {
            book.setGenres(bookDTO.getGenre());
        }
        if (bookDTO.getLinkToBook() != null) {
            book.setLinkToBook(bookDTO.getLinkToBook());
        }
        if (bookDTO.getPublisher() != null) {
            book.setPublisher(bookDTO.getPublisher());
        }
        bookRepository.save(book);
        return book;
    }

    public List<Book> getBooks() {
        return bookRepository.findAll();
    }

    public Book getBook(Long id) {
        return bookRepository.findById(id).orElse(null);
    }
}
