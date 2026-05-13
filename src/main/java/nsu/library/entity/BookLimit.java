package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Table(name="book_limits")
@RequiredArgsConstructor
@Getter
@Setter
public class BookLimit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name="group_id", referencedColumnName = "id")
    Group group;

    @ManyToOne
    @JoinColumn(name="book_id", referencedColumnName = "id")
    Book book;

    @Column(name="limit_num", nullable = false)
    long limit;

    public boolean decrementLimit() {
        if (limit <= 0) {
            return false;
        }
        limit -= 1;
        return true;
    }
}
