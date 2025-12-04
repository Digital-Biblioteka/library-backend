package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookDoc;
import nsu.library.dto.BookDTO;
import nsu.library.dto.SearchQuery;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import nsu.library.repository.GenreRepository;
import nsu.library.service.minio.MinioService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// add toc to book attributes?
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookImport bookImport;
    private final SearchService searchService;
    private final MinioService minioService;
    private final GenreRepository genreRepository;

    /**
     * Ручное добавление книги с заполнением всех полей и ее добавление в минио
     *
     * @param bookDTO заполненный админом дто книги
     * @param file файл самой книжки
     * @return созданный и сохраненный в бд объект книги
     */
    public Book addBookManually(BookDTO bookDTO, MultipartFile file) {
        String bookId = UUID.randomUUID().toString() + "." + file.getOriginalFilename();
        String fileName = minioService.loadBookEpub(file, bookId);

        byte[] cover = bookImport.getBookPreview(file);
        String coverName = minioService.loadBookCover(cover, bookId);

        Book book = createBookFromDTO(bookDTO, bookId);
        bookRepository.save(book);
        return book;
    }

    /**
     * Автоматическое извлечение метаданных из книги и ее добавление в бд и минио.
     * + добавление обложки в минио
     * + нужно имплементировать автоматическое добавление жанра, если его еще нет
     *
     * @param file просто файл книжки
     * @return созданную книжку
     */
    public Book addBookAuto(MultipartFile file) {
        String bookId = UUID.randomUUID().toString() + "." + file.getOriginalFilename();

        BookDTO book;
        try {
            book = bookImport.parseEpub(bookImport.readEpub(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Book ourBook = createBookFromDTO(book, bookId);

        String fileName = minioService.loadBookEpub(file, bookId);

        byte[] cover = bookImport.getBookPreview(file);
        String coverName = minioService.loadBookCover(cover, bookId);

        bookRepository.save(ourBook);
        return ourBook;
    }

    /**
     * Конверт dto в книжку
     *
     * @param bookDTO дто
     * @param link ссылка на книжку в минио
     * @return созданная книжка
     */
    public Book createBookFromDTO(BookDTO bookDTO, String link) {
        Book book = new Book();
        book.setTitle(bookDTO.getTitle());
        book.setAuthor(bookDTO.getAuthor());
        book.setDescription(bookDTO.getDescription());
        if (bookDTO.getIsbn() != null) {
            book.setIsbn(bookDTO.getIsbn());
        }
        book.setIsbn("12345"); // zaglushka ebani
        book.setPublisher(bookDTO.getPublisher());
//        if (bookDTO.getGenreId()!= null) {
//            Genre genre = genreRepository.getReferenceById(bookDTO.getGenreId());
//            book.setGenre(genre);
//        } fix this
        book.setLinkToBook(link);
        return book;
    }

    /**
     * Обратная операция. конверт из книги в дто для извлечения метаданных.
     *
     * @param book книжка
     * @return дто
     */
    public BookDTO convertBookToDTO(Book book) {
        BookDTO bookDTO = new BookDTO();
        bookDTO.setTitle(book.getTitle());
        bookDTO.setAuthor(book.getAuthor());
        bookDTO.setDescription(book.getDescription());
        bookDTO.setPublisher(book.getPublisher());
        if (book.getGenre() != null) {
            bookDTO.setGenreId(book.getGenre().getId());
        }
        return bookDTO;
    }

    public List<Book> searchBooks(SearchQuery searchQuery) {
        List<BookDoc> books = searchService.searchBooks(searchQuery);
        List<Book> searchBooks = new ArrayList<>();
        for (BookDoc bookDoc : books) {
            searchBooks.add(bookRepository.findByIsbn(bookDoc.isbn()));
        }
        return searchBooks;
    }

    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }

    public Book editBook(Long id, BookDTO bookDTO) {
        Book book = bookRepository.findById(id).orElseThrow();
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
        if (bookDTO.getGenreId() != null) {
            book.setGenre(genreRepository.findById(bookDTO.getGenreId())
                    .orElseThrow());
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
        return bookRepository.findById(id).orElseThrow();
    }
}
