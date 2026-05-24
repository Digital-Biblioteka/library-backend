package nsu.library.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ContentSearchQuery(
        String query,
        @JsonProperty("book_id") String bookId,
        Integer size
) {
    public ContentSearchQuery {
        if (size == null) {
            size = 10;
        }
    }
}