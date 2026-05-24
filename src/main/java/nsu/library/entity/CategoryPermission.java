package nsu.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

/**
 * Пользователь имеет доступ ко всем книгам в данной категории в рамках группы.
 * Действует до timeExpires (по умолчанию 30 дней).
 */
@Entity
@Table(name = "category_permissions",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "group_id", "category_id"}))
@NoArgsConstructor
@Getter
@Setter
public class CategoryPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    Group group;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    BookCategory category;

    @Column(name = "time_expires")
    Instant timeExpires;
}
