package nsu.library.dto.category;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsu.library.entity.BookCategory;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoriesForBookDTO {
    BookCategory category;
}
