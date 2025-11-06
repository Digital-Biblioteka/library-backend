package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class addBookDTO {
    ADDMode mode;
    String link;
    BookDTO bookDTO;

    public enum ADDMode{
        manual,
        auto;
    }
}
