package nsu.library.dto.search;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentSearchResult {
    private String bookId;
    private String title;
    private String author;
    private String chapter;
    private int chapterIndex;
    private int spineIndex;
    private int paragraphIndex;
    private String textSnippet;
    private double score;
}
