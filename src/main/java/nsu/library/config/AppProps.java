package nsu.library.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class AppProps {
    @Value("${app.es.url:http://localhost:9200}")
    private String esUrl;

    @Value("${app.embed.url:http://localhost:8000/embed}")
    private String embedUrl;

    @Value("${app.search.url:http://localhost:8001/search/books}")
    private String searchUrl;

    @Value("${app.search.index-url:http://localhost:8001}")
    private String searchIndexUrl;

    @Value("${app.search.content-url:http://localhost:8001/search/content}")
    private String searchContentUrl;

    @Value("${app.search.review-search-url:http://localhost:8001/search/reviews}")
    private String searchReviewUrl;

    @Value("${app.search.suggest-url:http://localhost:8001/suggest}")
    private String suggestUrl;

    @Value("${app.search.semantic-url:http://localhost:8001/search/books/semantic}")
    private String searchSemanticUrl;

    @Value("${app.search.ask-url:http://localhost:8001/ask-book}")
    private String askUrl;
}