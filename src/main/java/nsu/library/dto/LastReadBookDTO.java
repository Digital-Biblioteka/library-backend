package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
