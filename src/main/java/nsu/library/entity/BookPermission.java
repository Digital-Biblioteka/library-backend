package nsu.library.entity;

//получается у каждой книжки есть связанная с ней табличка разрешений типа ACL
// - бук ид, груп ид, юзер ид, время

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "book_permissions")
@Getter
@Setter
public class BookPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name="book_id", referencedColumnName = "id", nullable = false)
    Book book;

    @ManyToOne
    @JoinColumn(name="group_id", referencedColumnName = "id", nullable = false)
    Group group;

    @ManyToOne
    @JoinColumn(name="user_id", referencedColumnName = "id", nullable = false)
    User user;

    // timestamp В формате UTC чтоб не мучиться
    @Column(name="time_expires")
    Instant timeExpires;
}
