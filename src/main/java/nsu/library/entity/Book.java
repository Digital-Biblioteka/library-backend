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
    String title;
    String author;
    String publisher;
    String isbn;
    String description;
    String genres;
}
