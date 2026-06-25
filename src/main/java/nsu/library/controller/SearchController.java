package nsu.library.controller;

import nsu.library.dto.book.BookDTO;
import nsu.library.dto.search.AskBookRequest;
import nsu.library.dto.search.AskBookResponse;
import nsu.library.dto.search.ContentSearchQuery;
import nsu.library.dto.search.ContentSearchResult;
import nsu.library.dto.search.SearchQuery;
import nsu.library.service.search.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping("/books/semantic")
    public ResponseEntity<List<BookDTO>> semanticSearch(@RequestBody SearchQuery query) {
        return ResponseEntity.ok(searchService.semanticSearch(query));
    }

    @PostMapping("/content")
    public ResponseEntity<List<ContentSearchResult>> contentSearch(@RequestBody ContentSearchQuery query) {
        return ResponseEntity.ok(searchService.searchContent(query));
    }

    @PostMapping("/ask")
    public ResponseEntity<AskBookResponse> askBook(@RequestBody AskBookRequest req) {
        return ResponseEntity.ok(searchService.askBook(req));
    }

    @GetMapping("/suggest")
    public ResponseEntity<List<Map<String, Object>>> suggest(
            @RequestParam(defaultValue = "") String prefix,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(searchService.suggest(prefix, size));
    }
}