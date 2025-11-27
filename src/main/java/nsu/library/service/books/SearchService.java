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
        String mode = q.modeOrDefault();
        if ("knn".equals(mode)) {
            return searchBooksKnnDocs(q.query(), q.kOrDefault());
        }
        return searchBooksBM25Docs(q);
    }

    public List<BookDoc> searchBooksBM25Docs(SearchQuery req) {
        String url = props.getEsUrl() + "/books/_search";
        Map<String, Object> body = new HashMap<>();
        body.put("from", req.fromOrDefault());
        body.put("size", req.sizeOrDefault());
        Map<String, Object> mm = new HashMap<>();
        mm.put("query", req.query());
        mm.put("fields", List.of("title^3", "author^2", "description", "genre"));
        body.put("query", Map.of("multi_match", mm));
        Map<String, Object> resp = postJson(url, body);
        return toBookDocs(resp);
    }

    public List<BookDoc> searchBooksKnnDocs(String query, int k) {
        float[] vec = embed(query);
        String url = props.getEsUrl() + "/books/_search";
        Map<String, Object> body = new HashMap<>();
        Map<String, Object> knn = new HashMap<>();
        knn.put("field", "description_vector");
        knn.put("query_vector", toList(vec));
        knn.put("k", k);
        knn.put("num_candidates", Math.max(100, k * 10));
        body.put("knn", knn);
        Map<String, Object> resp = postJson(url, body);
        return toBookDocs(resp);
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
                    (String) src.get("genre"),
                    (String) src.get("linkToBook"),
                    (String) src.get("source_uid"),
                    (String) src.get("isbn"),
                    score
            ));
        }
        return out;
    }

    private float[] embed(String text) {
        try {
            Map<String, String> req = Map.of("text", text);
            HttpHeaders h = new HttpHeaders();
            h.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<Map> resp = http.postForEntity(props.getEmbedUrl(), new HttpEntity<>(req, h), Map.class);
            @SuppressWarnings("unchecked")
            Map<String, Object> body = (Map<String, Object>) resp.getBody();
            if (body == null) throw new RuntimeException("Empty embed response");
            @SuppressWarnings("unchecked")
            List<Number> vec = (List<Number>) body.get("vector");
            float[] out = new float[vec.size()];
            for (int i = 0; i < vec.size(); i++) out[i] = vec.get(i).floatValue();
            return out;
        } catch (Exception e) {
            throw new RuntimeException("Embed service failed: " + e.getMessage(), e);
        }
    }

    private List<Float> toList(float[] v) {
        List<Float> list = new ArrayList<>(v.length);
        for (float x : v) list.add(x);
        return list;
    }
}
