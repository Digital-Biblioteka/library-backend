package nsu.library.dto;

public record SearchQuery(
        String query,
        String title,
        String author,
        String genre,
        String description
) {}
