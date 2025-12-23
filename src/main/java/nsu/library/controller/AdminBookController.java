package nsu.library.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.book.BookDTO;
import nsu.library.dto.book.addBookDTO;
import nsu.library.service.books.BookService;
import nsu.library.service.search.SearchIndexClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import nsu.library.entity.Book;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AdminBookController {
    private final BookService bookService;
    private final SearchIndexClient searchIndexClient;

    @GetMapping("admin/books")
    public List<Book> getBooks() {
        return bookService.getBooks();
    }

    //@GetMapping("admin/search")
    //public List<Book> searchBooks(SearchQuery searchQuery) {
    //    return bookService.searchBooks(searchQuery);
    //}

    /**
     * Добавление книжки админом с двумя модами: авто и вручную.
     * Для описания логики see bookservice и соотв. методы
     *
     * @param file файл книжки
     * @param dtoJson де факто addBookDTO, но в виде джсона тк нельзя иметь и дто, и файл в параметрах
     * @return созданную книжку
     * @throws JsonProcessingException если не распарсим джсон из строки
     */
    @PostMapping(value = "admin/books",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Book addBook(@RequestPart("file") MultipartFile file, @RequestPart("addBookDTO") String dtoJson)
            throws JsonProcessingException {
        addBookDTO dto = new ObjectMapper().readValue(dtoJson, addBookDTO.class);

        if (dto.getMode() == addBookDTO.ADDMode.auto) {
            return bookService.addBookAuto(file);
        } else {
            if (dto.getBookDTO() == null) throw new IllegalArgumentException("bookDTO is required");
            return bookService.addBookManually(dto.getBookDTO(), file);
        }
    }

    @DeleteMapping("admin/books/{id}")
    public void deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
    }

    @PutMapping("admin/books/{id}")
    public Book updateBook(@PathVariable Long id, @RequestBody BookDTO dto) {
        System.out.println(dto.getAuthor());
        return bookService.editBook(id, dto);
    }

    @GetMapping("admin/books/elastic")
    public List<Book> putBooksInElastic() {
        List<Book> books = getBooks();
        for (Book book : books) {
            searchIndexClient.indexBook(book);
        }
        return books;
    }
}
