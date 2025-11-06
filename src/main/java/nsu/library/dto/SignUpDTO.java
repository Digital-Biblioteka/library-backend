package nsu.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
@Schema(description = "Запрос на регистрацию")
public class SignUpDTO {

    @Schema(description = "Имя пользователя", example = "Jon")
    @Size(min = 2, max = 50, message = "Имя пользователя должно содержать от 5 до 50 символов")
    @NotBlank(message = "Имя пользователя не может быть пустыми")
    private String username;

    @Schema(description = "Email", example = "myemail@gmail.com")
    @Size(min = 4, max = 255, message = "Длина почты должна быть не более 255 символов")
    private String email;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 4, max = 255, message = "Длина пароля должна быть не более 255 символов")
    private String password;
}