package nsu.library.dto.list;

import lombok.*;
import nsu.library.dto.book.BookDTO;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReadLaterBookDTO {
    Long userId;
    Long bookId;
    BookDTO book;
}
