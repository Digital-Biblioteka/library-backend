package nsu.library.security;

import nsu.library.entity.User;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Тестовая аннотация для подмены залогиненного пользователя в тестах ролевого доступа.
 * <p>
 * В отличие от стандартного {@code @WithMockUser}, эта аннотация кладёт в
 * {@link org.springframework.security.core.context.SecurityContext} принципал именно
 * {@link CustomUserDetails} — тот же класс, что использует {@link JwtAuthFilter} в проде.
 * Это важно: контроллеры берут пользователя через {@code @AuthenticationPrincipal CustomUserDetails},
 * и со стандартным {@code @WithMockUser} (principal = org.springframework.security.core.userdetails.User)
 * такие методы упадут с ошибкой приведения типов вместо того, чтобы реально проверить доступ по роли.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
@WithSecurityContext(factory = WithMockLibraryUser.Factory.class)
public @interface WithMockLibraryUser {

    long id() default 1L;

    String username() default "test-user";

    String email() default "test-user@library.local";

    User.ROLE role() default User.ROLE.ROLE_USER;

    class Factory implements WithSecurityContextFactory<WithMockLibraryUser> {
        @Override
        public SecurityContext createSecurityContext(WithMockLibraryUser annotation) {
            User user = new User();
            user.setId(annotation.id());
            user.setUsername(annotation.username());
            user.setEmail(annotation.email());
            user.setPassword("N/A");
            user.setRole(annotation.role());

            CustomUserDetails principal = new CustomUserDetails(user);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);
            return context;
        }
    }
}
