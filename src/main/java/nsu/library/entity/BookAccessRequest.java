package nsu.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "access_requests")
@NoArgsConstructor
@Getter
@Setter
public class BookAccessRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID ID;

    @ManyToOne
    @JoinColumn(name="book_id", referencedColumnName = "id", nullable = false)
    Book book;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name="group_id", referencedColumnName = "id", nullable = false)
    Group group;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    RequestStatus status;

    public enum RequestStatus {
        PENDING,
        APPROVED,
        REJECTED,
    }
}
