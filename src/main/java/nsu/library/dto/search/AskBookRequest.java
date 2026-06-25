package nsu.library.dto.search;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AskBookRequest(
        String question,
        @JsonProperty("book_id") String bookId,
        @JsonProperty("top_k") Integer topK
) {
    public AskBookRequest {
        if (topK == null) {
            topK = 10;
        }
    }
}
