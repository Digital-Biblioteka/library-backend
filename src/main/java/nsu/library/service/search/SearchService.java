package nsu.library.service.search;

import nsu.library.config.AppProps;
import nsu.library.dto.search.ContentSearchQuery;
import nsu.library.dto.search.ContentSearchResult;
import nsu.library.dto.search.SearchQuery;
import nsu.library.dto.book.BookDTO;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import nsu.library.repository.ReviewRepository;
import nsu.library.service.books.GenreService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService {
    private final RestTemplate http;
    private final AppProps props;
    private final GenreService genreService;
    private final ReviewRepository reviewRepository;
    private final BookRepository bookRepository;

    public SearchService(RestTemplate http, AppProps props, GenreService genreService,
                         ReviewRepository reviewRepository, BookRepository bookRepository) {
        this.http = http;
        this.props = props;
        this.genreService = genreService;
        this.reviewRepository = reviewRepository;
        this.bookRepository = bookRepository;
    }

    public List<BookDTO> searchBooks(SearchQuery q) {
        Set<Long> reviewBookIds = null;
        Map<Long, List<String>> reviewSnippetsMap = null;
        if (q.reviewQuery() != null && !q.reviewQuery().isBlank()) {
            String reviewUrl = props.getSearchReviewUrl();
            if (reviewUrl != null && !reviewUrl.isBlank()) {
                try {
                    HttpHeaders h = new HttpHeaders();
                    h.setContentType(MediaType.APPLICATION_JSON);
                    Map<String, Object> reviewBody = new HashMap<>();
                    reviewBody.put("query", q.reviewQuery());
                    reviewBody.put("size", 20);
                    ResponseEntity<List> resp = http.postForEntity(reviewUrl, new HttpEntity<>(reviewBody, h), List.class);
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> reviewResults = (List<Map<String, Object>>) resp.getBody();
                    if (reviewResults != null) {
                        reviewBookIds = new HashSet<>();
                        reviewSnippetsMap = new HashMap<>();
                        for (Map<String, Object> r : reviewResults) {
                            Object bid = r.get("book_id");
                            if (bid != null) {
                                Long bookId = bid instanceof Number n ? n.longValue() : Long.parseLong(bid.toString());
                                reviewBookIds.add(bookId);
                                @SuppressWarnings("unchecked")
                                List<String> reviews = (List<String>) r.get("reviews");
                                if (reviews != null && !reviews.isEmpty()) {
                                    reviewSnippetsMap.computeIfAbsent(bookId, k -> new ArrayList<>()).addAll(reviews);
                                } else {
                                    String text = (String) r.getOrDefault("review_text", "");
                                    if (!text.isBlank()) {
                                        reviewSnippetsMap.computeIfAbsent(bookId, k -> new ArrayList<>()).add(text);
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    List<Long> ids = reviewRepository.findBookIdsByReviewTextContaining(q.reviewQuery());
                    reviewBookIds = new HashSet<>(ids);
                }
            } else {
                List<Long> ids = reviewRepository.findBookIdsByReviewTextContaining(q.reviewQuery());
                reviewBookIds = new HashSet<>(ids);
            }
        }
        final Set<Long> finalReviewBookIds = reviewBookIds;
        final Map<Long, List<String>> finalReviewSnippets = reviewSnippetsMap;

        boolean hasTextSearch = q.query() != null && !q.query().isBlank()
                || q.title() != null && !q.title().isBlank()
                || q.author() != null && !q.author().isBlank()
                || q.genre() != null && !q.genre().isBlank()
                || q.description() != null && !q.description().isBlank();

        List<BookDTO> results;
        if (hasTextSearch) {
            String delegate = props.getSearchUrl();
            if (delegate != null && !delegate.isBlank()) {
                results = searchBooksExternal(delegate, q);
            } else {
                results = searchBooksBM25TopN(q.query(), 100);
            }
        } else {
            results = bookRepository.findAll().stream()
                    .map(this::toBookDTO)
                    .collect(Collectors.toList());
        }

        if (finalReviewBookIds != null) {
            results = results.stream()
                    .filter(b -> finalReviewBookIds.contains(b.getId()))
                    .collect(Collectors.toList());
        }

        if (q.minRating() != null && q.minRating() > 0) {
            List<Object[]> avgRatings = reviewRepository.findAverageRatingPerBook();
            Map<Long, Double> bookAvgRating = new HashMap<>();
            for (Object[] row : avgRatings) {
                Long bookId = (Long) row[0];
                Double avg = row[1] instanceof Number n ? n.doubleValue() : 0.0;
                bookAvgRating.put(bookId, avg);
            }
            double minR = q.minRating();
            results = results.stream()
                    .filter(b -> {
                        Double avg = bookAvgRating.get(b.getId());
                        return avg != null && avg >= minR;
                    })
                    .collect(Collectors.toList());
        }

        if (q.reviewQuery() != null && !q.reviewQuery().isBlank()) {
            if (finalReviewSnippets != null) {
                for (BookDTO dto : results) {
                    List<String> snippets = finalReviewSnippets.get(dto.getId());
                    if (snippets != null && !snippets.isEmpty()) {
                        dto.setReviewSnippets(snippets);
                    }
                }
            } else {
                List<Object[]> reviewData = reviewRepository.findBookReviewSnippetsByText(q.reviewQuery());
                String queryLower = q.reviewQuery().toLowerCase();
                String[] queryTerms = queryLower.split("\\s+");
                Map<Long, List<Map.Entry<String, Integer>>> bookScored = new HashMap<>();
                for (Object[] row : reviewData) {
                    Long bookId = (Long) row[0];
                    String text = (String) row[1];
                    if (text == null || text.isBlank()) continue;
                    String textLower = text.toLowerCase();
                    int score = 0;
                    for (String term : queryTerms) {
                        if (term.length() < 2) continue;
                        int idx = 0;
                        int cnt = 0;
                        while ((idx = textLower.indexOf(term, idx)) != -1) {
                            cnt++;
                            idx += term.length();
                        }
                        score += cnt;
                    }
                    bookScored.computeIfAbsent(bookId, k -> new ArrayList<>()).add(Map.entry(text, score));
                }
                for (BookDTO dto : results) {
                    List<Map.Entry<String, Integer>> scored = bookScored.get(dto.getId());
                    if (scored != null) {
                        scored.sort((a, b) -> b.getValue().compareTo(a.getValue()));
                        List<String> sortedSnippets = scored.stream()
                                .map(Map.Entry::getKey)
                                .collect(Collectors.toList());
                        dto.setReviewSnippets(sortedSnippets);
                    }
                }
            }
        }

        if (q.sortOrder() != null && !q.sortOrder().isBlank()) {
            Comparator<Double> ratingDir = q.sortOrder().equalsIgnoreCase("asc")
                    ? Comparator.naturalOrder()
                    : Comparator.reverseOrder();
            Comparator<BookDTO> byRating = Comparator.comparing(
                    BookDTO::getRating, Comparator.nullsLast(ratingDir));
            results.sort(byRating);
        }

        return results;
    }

    public List<BookDTO> semanticSearch(SearchQuery q) {
        String url = props.getSearchSemanticUrl();
        if (url == null || url.isBlank()) {
            return List.of();
        }
        return searchBooksExternal(url, q);
    }

    private BookDTO toBookDTO(Book book) {
        BookDTO dto = new BookDTO();
        dto.setId(book.getId());
        dto.setTitle(book.getTitle());
        dto.setAuthor(book.getAuthor());
        dto.setDescription(book.getDescription());
        dto.setPublisher(book.getPublisher());
        dto.setRating(book.getRating());
        if (book.getGenre() != null) {
            dto.setGenre(book.getGenre().getGenreName());
        }
        dto.setIndexingStatus(book.getIndexingStatus());
        return dto;
    }

    public List<ContentSearchResult> searchContent(ContentSearchQuery q) {
        String url = props.getSearchContentUrl();
        if (url == null || url.isBlank()) {
            return List.of();
        }
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<List> resp = http.postForEntity(url, new HttpEntity<>(q, h), List.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> arr = (List<Map<String, Object>>) resp.getBody();
        List<ContentSearchResult> out = new ArrayList<>();
        if (arr == null) return out;
        for (Map<String, Object> m : arr) {
            ContentSearchResult r = new ContentSearchResult();
            r.setBookId(String.valueOf(m.getOrDefault("book_id", "")));
            r.setTitle((String) m.getOrDefault("title", ""));
            r.setAuthor((String) m.getOrDefault("author", ""));
            r.setChapter((String) m.getOrDefault("chapter", ""));
            r.setChapterIndex(m.get("chapter_index") instanceof Number n ? n.intValue() : 0);
            r.setSpineIndex(m.get("spine_index") instanceof Number n ? n.intValue() : -1);
            r.setParagraphIndex(m.get("paragraph_index") instanceof Number n ? n.intValue() : 0);
            r.setTextSnippet((String) m.getOrDefault("text_snippet", ""));
            r.setScore(m.get("score") instanceof Number n ? n.doubleValue() : 0.0);
            out.add(r);
        }
        return out;
    }

    public List<Map<String, Object>> suggest(String prefix, int size) {
        String url = props.getSuggestUrl();
        if (url == null || url.isBlank()) {
            return List.of();
        }
        String fullUrl = url + "?prefix=" + java.net.URLEncoder.encode(prefix, java.nio.charset.StandardCharsets.UTF_8) + "&size=" + size;
        ResponseEntity<List> resp = http.getForEntity(fullUrl, List.class);
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> arr = (List<Map<String, Object>>) resp.getBody();
        return arr != null ? arr : List.of();
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
            Object bookId = m.get("book_id");
            if (bookId == null) {
                bookId = m.get("id");
            }
            if (bookId instanceof Number n) {
                dto.setId(n.longValue());
            } else if (bookId instanceof String s) {
                try {
                    dto.setId(Long.parseLong(s));
                } catch (NumberFormatException ignored) {
                }
            }
            dto.setTitle((String) m.get("title"));
            dto.setAuthor((String) m.get("author"));
            dto.setDescription((String) m.get("description"));
            dto.setGenre((String) m.get("genres"));
            dto.setPublisher((String) m.get("publisher"));
            if (dto.getId() != null) {
                bookRepository.findById(dto.getId()).ifPresent(book ->
                    dto.setIndexingStatus(book.getIndexingStatus())
                );
            }
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
            Object bookId = src.get("book_id");
            if (bookId == null) {
                bookId = src.get("id");
            }
            if (bookId instanceof Number n) {
                dto.setId(n.longValue());
            } else if (bookId instanceof String s) {
                try {
                    dto.setId(Long.parseLong(s));
                } catch (NumberFormatException ignored) {
                }
            }
            dto.setTitle((String) src.get("title"));
            dto.setAuthor((String) src.get("author"));
            dto.setDescription((String) src.get("description"));
            dto.setGenre((String) src.get("genres"));
            dto.setPublisher((String) src.get("publisher"));
            if (dto.getId() != null) {
                bookRepository.findById(dto.getId()).ifPresent(book ->
                    dto.setIndexingStatus(book.getIndexingStatus())
                );
            }
            out.add(dto);
        }
        return out;
    }
}