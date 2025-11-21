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
    String genre; //нужно в бд-шку добавить столбец с жанрами
    //переименовала с множественного на единственное число, т.к посчитала этот вариант
    //более правильным с точки зрения что это книга и жанр у нее один вот

    @Column(nullable = false)
    String linkToBook;
}
