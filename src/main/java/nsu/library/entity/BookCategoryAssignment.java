package nsu.library.entity;

import jakarta.persistence.*;
import lombok.*;
import nsu.library.util.BookCategoryId;

@Entity
@Table(name = "book_category_assignments")
@NoArgsConstructor
@Getter
@Setter
public class BookCategoryAssignment {

    @EmbeddedId
    private BookCategoryId id;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("bookId")
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id", nullable = false)
    private BookCategory category;
}
