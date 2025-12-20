package nsu.library.dto.reader;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsu.library.dto.book.BookDTO;

/**
 * Превью книжки.
 * Cover - ссылка на минио + данные
 */
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class BookPreviewDTO {
    String cover;
    BookDTO metadata;
}
