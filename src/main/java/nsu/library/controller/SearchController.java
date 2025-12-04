package nsu.library.controller;

import nsu.library.dto.BookDTO;
import nsu.library.dto.SearchQuery;
import nsu.library.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("/books")
    public ResponseEntity<List<BookDTO>> booksUnified(@RequestBody SearchQuery query) {
        return ResponseEntity.ok(searchService.searchBooks(query));
    }
}
