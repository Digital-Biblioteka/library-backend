package nsu.library.dto.category;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryLimitRequestDTO {
    UUID groupID;
    UUID categoryID;
    long requestedLimit;
}
