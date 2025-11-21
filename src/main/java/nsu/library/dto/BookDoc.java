package nsu.library.dto;

public record BookDoc(
        String book_id,
        String title,
        String author,
        String publisher,
        String description,
        String genres,
        String linkToBook,
        String source_uid,
        String isbn,
        Float score
) {}
