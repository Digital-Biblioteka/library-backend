package nsu.library.dto;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class addBookDTO {
    @NonNull
    ADDMode mode;
    @NonNull
    String link;
    BookDTO bookDTO;

    public enum ADDMode{
        manual,
        auto;
    }
}
