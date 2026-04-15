package nsu.library.service.search;

import lombok.RequiredArgsConstructor;
import nsu.library.config.AppProps;
import nsu.library.entity.Book;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SearchIndexClient {
    private final RestTemplate restTemplate;
    private final AppProps appProps;

    public void indexBook(Book book) {
        if (book == null || book.getId() == null) {
            return;
        }

        String base = appProps.getSearchIndexUrl();
        String url = base.endsWith("/")
                ? base + "index/book"
                : base + "/index/book";

        Map<String, Object> body = new HashMap<>();
        body.put("book_id", book.getId());
        body.put("title", book.getTitle());
        body.put("author", book.getAuthor());
        body.put("publisher", book.getPublisher());
        body.put("description", book.getDescription());
        body.put("genres", book.getGenre() != null ? book.getGenre().getGenreName() : null);
        body.put("linkToBook", book.getLinkToBook());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
        } catch (Exception e) {
            System.err.println("Failed to index book in search service: " + e.getMessage());
        }
    }

    public void deleteBook(Long bookId) {
        if (bookId == null) {
            return;
        }

        String base = appProps.getSearchIndexUrl();
        String url = base.endsWith("/")
                ? base + "index/book/" + bookId
                : base + "/index/book/" + bookId;

        try {
            restTemplate.delete(url);
        } catch (Exception e) {
            System.err.println("Failed to delete book from search service: " + e.getMessage());
        }
    }
}
