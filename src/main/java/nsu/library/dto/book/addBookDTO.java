package nsu.library.dto.book;

import lombok.*;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
public class addBookDTO {
    @NonNull
    ADDMode mode;
    BookDTO bookDTO;

    public enum ADDMode{
        manual,
        auto
    }
}
