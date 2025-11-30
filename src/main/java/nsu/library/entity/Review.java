package nsu.library.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumn(name = "books", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Book book;

    @JoinColumn(name = "users", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private User user;

    @Column(nullable = false)
    int rating;

    @Column
    String review_text;

}
