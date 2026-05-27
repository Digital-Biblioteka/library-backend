package nsu.library.dto.book;

import lombok.*;
import nsu.library.entity.Book;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class addBookDTO {
    @NonNull
    ADDMode mode;
    BookDTO bookDTO;
    Book.PublicityType publicityType;

    public enum ADDMode{
        manual,
        auto
    }
}
