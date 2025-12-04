package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nsu.library.util.LastReadId;

@Entity
@RequiredArgsConstructor
@Getter
@Setter
@IdClass(LastReadId.class)
@Table(name="last_read_books")
public class LastRead {
    @Id
    private Long userId;

    @Id
    private Long bookId;
}
