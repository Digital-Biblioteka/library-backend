package nsu.library.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumn(name = "book_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private Book book;

    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    @ManyToOne
    private User user;

    @Column(nullable = false)
    int rating;

    @Column
    String review_text;

}
