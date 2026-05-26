package nsu.library.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsu.library.entity.Book;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BooksInCategoryDTO {
    Book book;
}
