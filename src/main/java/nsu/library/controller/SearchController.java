package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookDTO;
import nsu.library.dto.SearchQuery;
import nsu.library.entity.Book;
import nsu.library.service.books.BookService;
import nsu.library.service.books.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;
    private final BookService bookService;

//    @PostMapping("/books")
//    public ResponseEntity<List<BookDTO>> booksUnified(@RequestBody SearchQuery query) {
//        return ResponseEntity.ok(searchService.searchBooks(query));
//    } это моя заглушка, тут надо ждать пока поиск заработает

    @GetMapping("/books")
    public List<Book> getBooks() {
        return bookService.getBooks();
    }

}
