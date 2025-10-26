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

    @NonNull
    String spine_reference;
    int paragraph_index;


    String text_bookmark;
}
