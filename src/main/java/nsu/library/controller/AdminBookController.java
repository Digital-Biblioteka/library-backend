package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookDTO;
import nsu.library.dto.SearchQuery;
import nsu.library.dto.addBookDTO;
import nsu.library.service.BookService;
import org.springframework.web.bind.annotation.*;
import nsu.library.entity.Book;

import java.util.List;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class AdminBookController {
    private final BookService bookService;

    @GetMapping("admin/books")
    public List<Book> getBooks() {
        return bookService.getBooks();
    }

    @GetMapping("admin/search")
    public List<Book> searchBooks(SearchQuery searchQuery) {
        return bookService.searchBooks(searchQuery);
    }

    @PostMapping("admin/books")
    public Book addBook(@RequestBody addBookDTO dto) {
        Book book = new Book();
        if (dto.getMode() == addBookDTO.ADDMode.auto) {
            book = bookService.addBookAuto(dto.getLink());
        } else if (dto.getMode() == addBookDTO.ADDMode.manual) {
            book = bookService.addBookManually(dto.getBookDTO());
        } else {
            throw new IllegalArgumentException("invalid adding book mode!" + dto.getMode() + addBookDTO.ADDMode.manual);
        }
        return book;
    }

    @DeleteMapping("admin/books")
    public void deleteBook(@RequestBody String isbn) {
        bookService.deleteBook(isbn);
    }

    @PutMapping("admin/books")
    public Book updateBook(@RequestBody BookDTO dto) {
        return bookService.editBook(dto.getIsbn(), dto);
    }
}
