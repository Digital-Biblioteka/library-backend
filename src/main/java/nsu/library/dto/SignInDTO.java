package nsu.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Schema(description = "Запрос на аутентификацию")
@NoArgsConstructor
public class SignInDTO {

    @Schema(description = "Email", example = "myemail@gmail.com")
    @Size(min = 4, max = 255, message = "Длина почты должна быть не более 255 символов")
    @NotBlank(message = "Почтовый адрес не может быть пустым")
    private String email;

    @Schema(description = "Пароль", example = "my_1secret1_password")
    @Size(min = 4, max = 255, message = "Длина пароля должна быть от 8 до 255 символов")
    @NotBlank(message = "Пароль не может быть пустыми")
    private String password;
}