package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChapterDTO {
    String html;
    int spineIdx;
    int totalSpines;
    boolean hasNext;
    boolean hasPrev;
}
