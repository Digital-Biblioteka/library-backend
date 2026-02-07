package nsu.library.config;

import io.minio.MinioClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MinioConfig {
    /**
     * бин с минио чтобы спринг его инджектил.
     *
     * @return клиент для работы с минио
     */
    @Bean
    public MinioClient minioClient() {
        String endpoint = getenvOr("MINIO_ENDPOINT", "http://minio:9000");
        if (!endpoint.contains("://")) {
            endpoint = "http://" + endpoint;
        }
        String access = getenvOr("MINIO_ACCESS_KEY", getenvOr("MINIO_ROOT_USER", "minioadmin"));
        String secret = getenvOr("MINIO_SECRET_KEY", getenvOr("MINIO_ROOT_PASSWORD", "minioadmin"));
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(access, secret)
                .build();
    }

    private static String getenvOr(String k, String def) {
        String v = System.getenv(k);
        return v != null && !v.isEmpty() ? v : def;
    }
}
