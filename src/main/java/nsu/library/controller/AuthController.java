package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.user.JwtAuthResponse;
import nsu.library.dto.user.SignInDTO;
import nsu.library.dto.user.SignUpDTO;
import nsu.library.service.system.AuthService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class AuthController {
    private final AuthService authenticationService;


    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthResponse signUp(@RequestBody @Valid SignUpDTO request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthResponse signIn(@RequestBody @Valid SignInDTO request) {
        return authenticationService.signIn(request);
    }
}
