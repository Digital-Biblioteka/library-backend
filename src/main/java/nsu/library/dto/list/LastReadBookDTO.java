package nsu.library.dto.list;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsu.library.dto.book.BookDTO;

/**
 * ДТО для отображения элемента в списке последних прочитанных книг.
 * userId - чей это список
 * bookDTO - для отображения метаданных книжек
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LastReadBookDTO {
    Long userId;
    Long bookId;
    BookDTO book;
}
