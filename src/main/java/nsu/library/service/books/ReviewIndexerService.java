package nsu.library.service.books;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsu.library.config.AppProps;
import nsu.library.entity.Review;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewIndexerService {

    private final RestTemplate http;
    private final AppProps props;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public void indexReview(Review review) {
        String url = props.getSearchIndexUrl() + "/index/reviews";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> reviewDoc = Map.of(
            "review_id", review.getId(),
            "book_id", String.valueOf(review.getBook().getId()),
            "user_id", review.getUser().getId(),
            "rating", review.getRating(),
            "review_text", review.getReview_text() != null ? review.getReview_text() : ""
        );

        Map<String, Object> body = Map.of("reviews", List.of(reviewDoc));

        try {
            String json = objectMapper.writeValueAsString(body);
            HttpEntity<String> entity = new HttpEntity<>(json, headers);
            http.postForEntity(url, entity, String.class);
            log.debug("Indexed review {} for book {}", review.getId(), review.getBook().getId());
        } catch (Exception e) {
            log.warn("Failed to index review {}: {}", review.getId(), e.getMessage());
        }
    }

    public void deleteReview(Long reviewId) {
        String url = props.getEsUrl() + "/reviews/_doc/" + reviewId;
        try {
            http.delete(url);
            log.debug("Deleted review {} from ES index", reviewId);
        } catch (Exception e) {
            log.warn("Failed to delete review {} from ES: {}", reviewId, e.getMessage());
        }
    }
}