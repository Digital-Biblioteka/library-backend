package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import nl.siegmann.epublib.domain.Resource;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class BookPreviewDTO {
    Resource cover;
    BookDTO metadata;
}
