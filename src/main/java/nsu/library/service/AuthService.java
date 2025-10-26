package nsu.library.service;


import lombok.RequiredArgsConstructor;
import nsu.library.dto.JwtAuthResponse;
import nsu.library.dto.SignInDTO;
import nsu.library.dto.SignUpDTO;
import nsu.library.entity.User;
import nsu.library.repository.UserRepository;
import nsu.library.security.CustomUserDetails;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthResponse signUp(SignUpDTO request) {
        if (userRepository.getUsersByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("Пользователь с таким именем уже существует");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(User.ROLE.ROLE_USER);
        userRepository.save(user); //.......bruh
        System.out.println("user created!");
        CustomUserDetails userDetails = new CustomUserDetails(user);
        var jwt = jwtService.generateToken(userDetails);

        return new JwtAuthResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthResponse signIn(SignInDTO request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = customUserDetailsService.getByUsername(request.getUsername());
        System.out.println("user found");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(auth.getAuthorities());

        var jwt = jwtService.generateToken(user);
        return new JwtAuthResponse(jwt);
    }
}