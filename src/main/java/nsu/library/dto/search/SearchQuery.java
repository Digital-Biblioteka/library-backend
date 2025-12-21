package nsu.library.dto.search;

public record SearchQuery(
        String query,
        String title,
        String author,
        String genre,
        String description
) {}
