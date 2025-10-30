package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import nsu.library.entity.User;

@Data
@AllArgsConstructor

public class UserDTO {
    Long id;
    String userName;
    String email;
    User.ROLE role;
}
