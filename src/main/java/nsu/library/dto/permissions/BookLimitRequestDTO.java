package nsu.library.dto.permissions;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BookLimitRequestDTO {
    UUID groupID;
    Long bookID;
    long requestedLimit;
}
