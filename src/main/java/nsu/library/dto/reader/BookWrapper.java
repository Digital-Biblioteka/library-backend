package nsu.library.dto.reader;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nl.siegmann.epublib.domain.SpineReference;
import nsu.library.dto.book.BookDTO;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
public class BookWrapper {
    BookDTO book;
    Map<String, SpineReference> mapSpineLink;
    List<SpineReference> spines;
}
