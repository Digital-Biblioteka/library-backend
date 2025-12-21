package nsu.library.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import nsu.library.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateUserDTO {
    String name;
    String password;
    String email;
    User.ROLE role;
}
