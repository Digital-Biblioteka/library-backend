package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import nl.siegmann.epublib.domain.Resource;

@AllArgsConstructor
@Getter
public class BookPreviewDTO {
    Resource cover;
    BookDTO metadata;
}
