package nsu.library.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;
import lombok.Setter;
import nsu.library.util.ReadingPositionId;

/**
 * Текущая позиция пользователя в конкретной книге.
 * primary key = composite key of user id and book id
 */
@IdClass(ReadingPositionId.class)
@Entity
@Getter
@Setter
public class ReadingPosition {
    @Id
    private Long userId;
    @Id
    private Long bookId;

    private String position;
}
