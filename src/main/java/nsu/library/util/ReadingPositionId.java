package nsu.library.util;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@Embeddable
public class ReadingPositionId implements Serializable {
    private Long userId;
    private Long bookId;
}
