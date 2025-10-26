package nsu.library.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import nsu.library.entity.User;

@Data
@AllArgsConstructor
public class CreateUserDTO {
    String name;
    String password;
    String email;
    User.ROLE role;
}
