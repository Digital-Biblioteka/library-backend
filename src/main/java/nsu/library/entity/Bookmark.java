package nsu.library.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "bookmarks")
public class Bookmark {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @JoinColumn(name = "BookId", nullable = false)
    @ManyToOne
    private Book book;

    @JoinColumn(name = "UserId", nullable = false)
    @ManyToOne
    private User user;
}
