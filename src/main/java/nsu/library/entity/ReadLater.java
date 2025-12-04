package nsu.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsu.library.util.ReadLaterId;

@Entity
@IdClass(ReadLaterId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="read_later")
public class ReadLater {
    @Id
    ReadLaterId id;

    @ManyToOne
    @JoinColumn(name="user_id")
    User user;

    @ManyToOne
    @JoinColumn(name="book_id")
    Book book;
}
