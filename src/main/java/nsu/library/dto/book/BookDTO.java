package nsu.library.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsu.library.entity.Book;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookDTO {
    Long id;
    String title;
    String author;
    String description;
    String genre;
    String publisher;
    Double rating;
    Book.PublicityType publicity;
    List<String> reviewSnippets;
}
