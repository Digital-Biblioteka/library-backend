package nsu.library.entity;

import jakarta.persistence.*;
import lombok.NonNull;

@Entity
@Table(name = "bookmarks")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @JoinColumn(name = "UserId", nullable = false)
    @ManyToOne
    private User user;

    @JoinColumn(name = "BookId", nullable = false)
    @ManyToOne
    private Book book;

    @Column(nullable = false)
    String spine_reference;

    @Column(nullable = false)
    int paragraph_index;

    String text_bookmark;
}
