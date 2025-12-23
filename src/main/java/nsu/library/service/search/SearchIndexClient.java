package nsu.library.service.search;

import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${search.service.url:http://localhost:8001}")
    private String searchServiceUrl;

    public void indexBook(Book book) {
        if (book == null || book.getId() == null) {
            return;
        }

        String url = searchServiceUrl.endsWith("/")
                ? searchServiceUrl + "index/book"
                : searchServiceUrl + "/index/book";

        Map<String, Object> body = new HashMap<>();
        body.put("book_id", book.getId());
        body.put("title", book.getTitle());
        body.put("author", book.getAuthor());
        body.put("publisher", book.getPublisher());
        body.put("description", book.getDescription());
        body.put("genres", null);
        body.put("linkToBook", book.getLinkToBook());
        body.put("isbn", book.getIsbn());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        try {
            restTemplate.postForEntity(url, request, Void.class);
        } catch (Exception e) {
            // для учебного проекта: просто логируем/игнорируем ошибку индексации
            System.err.println("Failed to index book in search service: " + e.getMessage());
        }
    }
}
