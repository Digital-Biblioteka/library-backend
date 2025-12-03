package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.siegmann.epublib.domain.Resource;

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
