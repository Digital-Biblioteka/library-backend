package nsu.library.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Сколько раз группа может выдать разрешение на категорию пользователям.
 * Декрементируется при апруве CategoryAccessRequest.
 */
@Entity
@Table(name = "category_limits",
       uniqueConstraints = @UniqueConstraint(columnNames = {"group_id", "category_id"}))
@NoArgsConstructor
@Getter
@Setter
public class CategoryLimit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    Group group;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    BookCategory category;

    @Column(name = "limit_num", nullable = false)
    long limit;

    public boolean decrementLimit() {
        if (limit <= 0) return false;
        limit -= 1;
        return true;
    }
}
