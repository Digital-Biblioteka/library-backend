package nsu.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "book_categories")
@NoArgsConstructor
@Getter
@Setter
public class BookCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @Column(nullable = false, unique = true)
    String name;

    @Column
    String description;

    /** Кто создал категорию (библиотекарь или админ) */
    @ManyToOne
    @JoinColumn(name = "created_by", referencedColumnName = "id")
    User createdBy;
}
