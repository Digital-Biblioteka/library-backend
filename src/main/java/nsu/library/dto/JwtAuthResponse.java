package nsu.library.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
<<<<<<< HEAD
=======
<<<<<<< HEAD
>>>>>>> 5448f6c89097dd64c72a9a10ac2cc0289e3be4aa
import lombok.Data;

@AllArgsConstructor
@Data
<<<<<<< HEAD
=======
=======

@Builder
@AllArgsConstructor
>>>>>>> fa885dc6924d75ca32739e6cca7784c2f03e7d91
>>>>>>> 5448f6c89097dd64c72a9a10ac2cc0289e3be4aa
@Schema(description = "Ответ c токеном доступа")
public class JwtAuthResponse {
    @Schema(description = "Токен доступа", example = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImV4cCI6MTYyMjUwNj...")
    private String token;
}
