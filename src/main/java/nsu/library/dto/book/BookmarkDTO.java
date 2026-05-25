package nsu.library.dto.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookmarkDTO {
    int spineRef;
    int paragraphIdx;
    UUID groupID;
    String text;
}
