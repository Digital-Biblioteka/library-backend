package nsu.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Пользователь просит у библиотекаря доступ к категории в рамках группы.
 */
@Entity
@Table(name = "category_access_requests")
@NoArgsConstructor
@Getter
@Setter
public class CategoryAccessRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name = "group_id", referencedColumnName = "id", nullable = false)
    Group group;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "id", nullable = false)
    BookCategory category;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    RequestStatus status = RequestStatus.PENDING;

    public enum RequestStatus { PENDING, APPROVED, REJECTED }
}
