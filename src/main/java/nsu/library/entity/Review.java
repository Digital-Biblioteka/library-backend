package nsu.library.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumn(name = "BookId", nullable = false)
    @ManyToOne
    private Book book;

    @JoinColumn(name = "UserId", nullable = false)
    @ManyToOne
    private User user;

    @Column(nullable = false)
    int rating;

    @Column
    String review_text;

}
