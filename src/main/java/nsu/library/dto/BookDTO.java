package nsu.library.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

// name, author, year, description, genre, publishing house of the book.
//@Getter
//@Setter
@Data
public class BookDTO {
    String title;
    String author;
    String description;
    String genre;
    String publisher;
    String isbn;
}
