package nsu.library.dto.category;

import lombok.*;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryAccessRequestDTO {
    UUID groupID;
    UUID categoryID;
}
