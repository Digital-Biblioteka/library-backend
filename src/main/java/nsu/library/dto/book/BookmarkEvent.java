package nsu.library.dto.book;

import lombok.*;

import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookmarkEvent {
    EventType type;
    long id;
    long userID;
    UUID groupID;
    int spineRef;
    int paragraphIndex;
    String text;

    public enum EventType {
        CREATED,
        UPDATED,
        DELETED
    }
}
