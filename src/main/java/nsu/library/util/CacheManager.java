package nsu.library.util;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("bookWrapperCache");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(100)          // максимум 100 книг в кэше
                .expireAfterAccess(30, TimeUnit.MINUTES)); // время жизни
        return cacheManager;
    }
}
