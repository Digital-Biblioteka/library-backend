package nsu.library.util;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Getter
@Setter
public class ReadLaterId {
    private Long userId;
    private Long bookId;
}
