 package nsu.library.service.books;

import nsu.library.config.AppProps;
import nsu.library.dto.SearchQuery;
import nsu.library.dto.BookDTO;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchService {
    private final RestTemplate http;
    private final AppProps props;

    public SearchService(RestTemplate http, AppProps props) {
        this.http = http;
        this.props = props;
    }

    public List<BookDTO> searchBooks(SearchQuery q) {
        String delegate = props.getSearchUrl();
        if (delegate != null && !delegate.isBlank()) {
            return searchBooksExternal(delegate, q);
        }
        return searchBooksBM25TopN(q.query(), 20);
    }

    private List<BookDTO> searchBooksBM25TopN(String query, int size) {
        String url = props.getEsUrl() + "/books/_search";
        Map<String, Object> body = new HashMap<>();
        body.put("from", 0);
        body.put("size", size);
        Map<String, Object> mm = new HashMap<>();
        mm.put("query", query);
        mm.put("fields", List.of("title^3", "author^2", "description", "genres"));
        body.put("query", Map.of("multi_match", mm));
        Map<String, Object> resp = postJson(url, body);
        return toBookDTOs(resp);
    }

    private List<BookDTO> searchBooksExternal(String url, SearchQuery query) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List> resp = http.postForEntity(url, new HttpEntity<>(query, h), List.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> arr = (List<Map<String, Object>>) resp.getBody();
        List<BookDTO> out = new ArrayList<>();
        if (arr == null) return out;
        for (Map<String, Object> m : arr) {
            BookDTO dto = new BookDTO();
            dto.setTitle((String) m.get("title"));
            dto.setAuthor((String) m.get("author"));
            dto.setDescription((String) m.get("description"));
            //dto.setGenre((String) m.get("genres")); add id here later
            dto.setPublisher((String) m.get("publisher"));
            dto.setIsbn((String) m.get("isbn"));
            //dto.setLinkToBook((String) m.get("linkToBook")); в дто нет такого поля
            out.add(dto);
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> postJson(String url, Map<String, Object> body) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Map> resp = http.postForEntity(url, new HttpEntity<>(body, h), Map.class);
        return (Map<String, Object>) resp.getBody();
    }

    @SuppressWarnings("unchecked")
    private List<BookDTO> toBookDTOs(Map<String, Object> body) {
        List<BookDTO> out = new ArrayList<>();
        if (body == null) return out;
        Map<String, Object> hits = (Map<String, Object>) body.get("hits");
        if (hits == null) return out;
        List<Map<String, Object>> list = (List<Map<String, Object>>) hits.get("hits");
        if (list == null) return out;
        for (Map<String, Object> h : list) {
            Map<String, Object> src = (Map<String, Object>) h.get("_source");
            if (src == null) continue;
            BookDTO dto = new BookDTO();
            dto.setTitle((String) src.get("title"));
            dto.setAuthor((String) src.get("author"));
            dto.setDescription((String) src.get("description"));
            //dto.setGenre((String) src.get("genres")); fix to id
            dto.setPublisher((String) src.get("publisher"));
            dto.setIsbn((String) src.get("isbn"));
            //dto.setLinkToBook((String) src.get("linkToBook")); no such method at all
            out.add(dto);
        }
        return out;
    }
}
