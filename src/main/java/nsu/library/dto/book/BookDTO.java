package nsu.library.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
    List<String> reviewSnippets;
}
