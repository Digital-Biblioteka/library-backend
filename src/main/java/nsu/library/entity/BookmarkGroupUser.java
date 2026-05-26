package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nsu.library.util.BookmarkGroupUserId;

@Entity
@Table(name = "bookmark_group_user")
@RequiredArgsConstructor
@Getter
@Setter
public class BookmarkGroupUser {
    @EmbeddedId
    private BookmarkGroupUserId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("groupId")
    @JoinColumn(name ="group_id", nullable = false)
    private BookmarkGroup group;
}
