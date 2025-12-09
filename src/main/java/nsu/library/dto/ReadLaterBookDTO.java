package nsu.library.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReadLaterBookDTO {
    Long userId;
    Long bookId;
    BookDTO book;
}
