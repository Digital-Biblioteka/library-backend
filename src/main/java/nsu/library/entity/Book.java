package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

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

    @Column
    String genres;

    @Column(nullable = false)
    String linkToBook;
}
