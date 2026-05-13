package nsu.library.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "access_requests")
@NoArgsConstructor
@Getter
@Setter
public class BookAccessRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String ID;

    @ManyToOne
    @JoinColumn(name="book_id", referencedColumnName = "id", nullable = false)
    Book book;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id", nullable = false)
    User user;

    @ManyToOne
    @JoinColumn(name="group_id", referencedColumnName = "id", nullable = false)
    Group group;
}
