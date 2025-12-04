package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@Table(name ="books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    String title;

    @Column
    String author;

    @Column
    String publisher;

    @Column(nullable = false)
    String isbn;

    @Column
    String description;

    @JoinColumn(name = "genre", referencedColumnName = "id")
    @ManyToOne
    private Genre genre;

    @Column(nullable = false)
    String linkToBook;
}
