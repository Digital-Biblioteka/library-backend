package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

// name, author, year, description, genre, publishing house of the book.
@AllArgsConstructor
@Getter
public class BookDTO {
    String title;
    String author;
    String description;
    String genre;
    String publisher;
    String isbn;
    String linkToBook;
}
