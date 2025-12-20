package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "bookmarks")
@Getter
@Setter
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @JoinColumn(name = "user_id", nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(name = "book_id", nullable = false)
    @ManyToOne
    private Book book;

    @Column(nullable = false)
    int spine_reference;

    @Column(nullable = false)
    int paragraph_index;

    String text_bookmark;
}
