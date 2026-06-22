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
//добавить поля в дто
//startOffset, endOffset, selectedText, note, color
public class BookmarkDTO {
    int spineRef;
    int paragraphIdx;
    long startOffset;
    long endOffset;

    UUID groupID;
    String text;
    String selectedText;
    String color;
}
