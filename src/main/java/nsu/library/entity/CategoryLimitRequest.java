package nsu.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Библиотекарь просит у админа N разрешений на категорию для своей группы.
 */
@Entity
@Table(name = "category_limit_requests")
@NoArgsConstructor
@Getter
@Setter
public class CategoryLimitRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    Group group;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    BookCategory category;

    @Column(name = "requested_limit", nullable = false)
    long requestedLimit;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    RequestStatus status = RequestStatus.PENDING;

    public enum RequestStatus { PENDING, APPROVED, REJECTED }
}
