package nsu.library.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppProps {
    @Value("${app.es.url:http://localhost:9200}")
    private String esUrl;

    @Value("${app.embed.url:http://localhost:8000/embed}")
    private String embedUrl;

    public String getEsUrl() {
        return esUrl;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }
}
