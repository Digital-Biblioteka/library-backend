package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "bookmarks")
@Getter
@Setter
//добавить поля в дто
//startOffset, endOffset, selectedText, note, color
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @JoinColumn(name = "group_id", nullable = true)
    @ManyToOne(fetch = FetchType.EAGER)
    BookmarkGroup group;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(name = "book_id", nullable = false)
    @ManyToOne
    private Book book;

    @JoinColumn(name = "start_offset")
    Long startOffset;

    @JoinColumn(name = "end_offset")
    Long endOffset;

    @Column(nullable = false)
    int spine_reference;

    @Column(nullable = false)
    int paragraph_index;

    @Column()
    String text_bookmark;

    @Column()
    String selectedText;

    @Column()
    String color;
}
