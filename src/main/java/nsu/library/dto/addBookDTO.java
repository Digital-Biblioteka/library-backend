package nsu.library.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@NoArgsConstructor
@Getter
public class addBookDTO {
    @NonNull
    ADDMode mode;
    BookDTO bookDTO;

    public enum ADDMode{
        manual,
        auto;
    }
}
