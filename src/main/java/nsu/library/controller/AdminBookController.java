package nsu.library.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookDTO;
import nsu.library.dto.SearchQuery;
import nsu.library.dto.addBookDTO;
import nsu.library.service.BookService;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import nsu.library.entity.Book;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping(
            value = "admin/books",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public Book addBook(
            @RequestPart("file") MultipartFile file,
            @RequestPart("addBookDTO") String dtoJson
    ) throws JsonProcessingException {
        addBookDTO dto = new ObjectMapper().readValue(dtoJson, addBookDTO.class);

        if (dto.getMode() == addBookDTO.ADDMode.auto) {
            return bookService.addBookAuto(file);
        } else {
            if (dto.getBookDTO() == null) throw new IllegalArgumentException("bookDTO is required");
            return bookService.addBookManually(dto.getBookDTO(), file);
        }
    }

    @DeleteMapping("admin/books")
    public void deleteBook(@RequestBody String isbn) {
        bookService.deleteBook(isbn);
    }

    @PutMapping("admin/books")
    public Book updateBook(@RequestBody BookDTO dto) {
        return bookService.editBook(dto.getIsbn(), dto);
    }

    @GetMapping("debug")
    public String debug() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return "Name: " + auth.getName() + ", authorities: " + auth.getAuthorities();
    }
}
