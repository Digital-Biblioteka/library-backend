package nsu.library.util;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class BookCategoryId implements Serializable {
    private Long bookId;
    private UUID categoryId;
}
