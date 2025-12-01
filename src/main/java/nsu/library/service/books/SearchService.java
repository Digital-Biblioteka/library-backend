 package nsu.library.service.books;

import nsu.library.config.AppProps;
import nsu.library.dto.SearchQuery;
import nsu.library.dto.BookDoc;
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

    public List<BookDoc> searchBooks(SearchQuery q) {
        String delegate = props.getSearchUrl();
        if (delegate != null && !delegate.isBlank()) {
            return searchBooksExternal(delegate, q.query());
        }
        return searchBooksBM25TopN(q.query(), 20);
    }

    private List<BookDoc> searchBooksBM25TopN(String query, int size) {
        String url = props.getEsUrl() + "/books/_search";
        Map<String, Object> body = new HashMap<>();
        body.put("from", 0);
        body.put("size", size);
        Map<String, Object> mm = new HashMap<>();
        mm.put("query", query);
        mm.put("fields", List.of("title^3", "author^2", "description", "genres"));
        body.put("query", Map.of("multi_match", mm));
        Map<String, Object> resp = postJson(url, body);
        return toBookDocs(resp);
    }

    private List<BookDoc> searchBooksExternal(String url, String query) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        Map<String, Object> req = Map.of("query", query);
        ResponseEntity<List> resp = http.postForEntity(url, new HttpEntity<>(req, h), List.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> arr = (List<Map<String, Object>>) resp.getBody();
        List<BookDoc> out = new ArrayList<>();
        if (arr == null) return out;
        for (Map<String, Object> m : arr) {
            Number sc = (Number) m.get("score");
            out.add(new BookDoc(
                    (String) m.get("book_id"),
                    (String) m.get("title"),
                    (String) m.get("author"),
                    (String) m.get("publisher"),
                    (String) m.get("description"),
                    (String) m.get("genres"),
                    (String) m.get("linkToBook"),
                    (String) m.get("source_uid"),
                    (String) m.get("isbn"),
                    sc == null ? null : sc.floatValue()
            ));
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
    private List<BookDoc> toBookDocs(Map<String, Object> body) {
        List<BookDoc> out = new ArrayList<>();
        if (body == null) return out;
        Map<String, Object> hits = (Map<String, Object>) body.get("hits");
        if (hits == null) return out;
        List<Map<String, Object>> list = (List<Map<String, Object>>) hits.get("hits");
        if (list == null) return out;
        for (Map<String, Object> h : list) {
            Map<String, Object> src = (Map<String, Object>) h.get("_source");
            Number sc = (Number) h.get("_score");
            Float score = sc == null ? null : sc.floatValue();
            if (src == null) continue;
            out.add(new BookDoc(
                    (String) src.get("book_id"),
                    (String) src.get("title"),
                    (String) src.get("author"),
                    (String) src.get("publisher"),
                    (String) src.get("description"),
                    (String) src.get("genres"),
                    (String) src.get("linkToBook"),
                    (String) src.get("source_uid"),
                    (String) src.get("isbn"),
                    score
            ));
        }
        return out;
    }
}
