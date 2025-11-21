package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

// name, author, year, description, genre, publishing house of the book.
@Getter
@Setter
public class BookDTO {
    String title;
    String author;
    String description;
    String genre;
    String publisher;
    String isbn;
}
