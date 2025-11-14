package nsu.library.service;

import lombok.AllArgsConstructor;
import nsu.library.repository.UserRepository;
import nsu.library.security.CustomUserDetails;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    public CustomUserDetails getByEmail(String email) {
        return new CustomUserDetails(userRepository.getUsersByEmail(email));
    }
    /**
     * Получение пользователя по имени пользователя
     * <p>
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    public org.springframework.security.core.userdetails.UserDetailsService userDetailsService() {
        return this::getByEmail;
    }

    /**
     * Получение текущего пользователя
     *
     * @return текущий пользователь
     */
    public CustomUserDetails getCurrentUser() {
        // Получение имени пользователя из контекста Spring Security
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByEmail(username);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        var user = userRepository.getUsersByEmail(email);

        /*при загрузке книги происходит проверка на админа(SecurityConfig)
        * и короче после долгих манипуляций все попадает сюда
        * но тут типо получается что вот пользователь ivan у него емал ivan@mail.ru
        * типо при регистрации сюда var user = userRepository.getUsersByEmail(email);
        * попадает именно его емаил и все ок, НОООООООООООООООООООООООООООООО
        * при загрузке книги сюда попадает его USERNAME поэтому загрузка падает с ошибкой
        * пользователь не найден, потому что его ищут по имейлу, а получают в аргументы имя, мяу
        * я временно для теста убрала проверку на админа, потому что я ебала, простите*/
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + email);
        }
        return new CustomUserDetails(user);
    }

}
