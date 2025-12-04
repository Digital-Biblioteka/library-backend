package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class LastRead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @JoinColumn(name="user_id", referencedColumnName = "id")
    @ManyToOne
    User user;

    @JoinColumn(name="book_id", referencedColumnName = "id")
    @ManyToOne
    Book book;
}
