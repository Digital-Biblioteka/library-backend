package nsu.library.security;

import nsu.library.config.SecurityConfig;
import nsu.library.controller.AdminBookController;
import nsu.library.controller.CategoryController;
import nsu.library.controller.GroupController;
import nsu.library.controller.LibrarianController;
import nsu.library.controller.UserController;
import nsu.library.entity.User;
import nsu.library.service.bookpermissions.AccessControlService;
import nsu.library.service.bookpermissions.BookCategoryService;
import nsu.library.service.bookpermissions.CategoryPermissionService;
import nsu.library.service.bookpermissions.LimitService;
import nsu.library.service.bookpermissions.PermissionService;
import nsu.library.service.books.BookService;
import nsu.library.service.groups.GroupService;
import nsu.library.service.search.SearchIndexClient;
import nsu.library.service.system.CustomUserDetailsService;
import nsu.library.service.system.JwtService;
import nsu.library.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Тесты ролевого доступа (RBAC) для трёх ролей системы: ROLE_USER, ROLE_LIBRARIAN, ROLE_ADMIN,
 * а также для неаутентифицированных запросов.
 * <p>
 * Цель — зафиксировать в виде тестов то, что сейчас настроено в
 * {@link SecurityConfig} (защита по пути, например {@code /api/admin/**}) и через
 * {@code @PreAuthorize} в контроллерах, чтобы случайная правка в одном из этих мест
 * не открыла доступ не той роли (или, наоборот, не закрыла доступ той, у которой он должен быть).
 * <p>
 * Поднимается только web-слой ({@code @WebMvcTest}) — без реальной БД, MinIO, Liquibase
 * и т.п. Все сервисы замоканы, нас интересует только то, кого Spring Security пропускает
 * до контроллера и кого режет на 403.
 */
@WebMvcTest(controllers = {
        AdminBookController.class,
        GroupController.class,
        LibrarianController.class,
        UserController.class,
        CategoryController.class
})
@Import({SecurityConfig.class, JwtAuthFilter.class})
class RoleBasedAccessControlTest {

    @Autowired
    private MockMvc mockMvc;

    // Зависимости JwtAuthFilter / SecurityConfig
    @MockBean
    private JwtService jwtService;
    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    // Зависимости контроллеров под тестом
    @MockBean
    private BookService bookService;
    @MockBean
    private SearchIndexClient searchIndexClient;
    @MockBean
    private GroupService groupService;
    @MockBean
    private UserService userService;
    @MockBean
    private LimitService limitService;
    @MockBean
    private CategoryPermissionService categoryPermissionService;
    @MockBean
    private AccessControlService accessControlService;
    @MockBean
    private PermissionService permissionService;
    @MockBean
    private BookCategoryService bookCategoryService;

    private static final String CATEGORY_BODY = "{\"name\":\"Fiction\",\"description\":\"desc\"}";

    @Nested
    @DisplayName("Публичные эндпоинты — доступны без токена")
    class PublicEndpoints {

        @Test
        @DisplayName("GET /api/books доступен анонимно")
        void getBooksIsPublic() throws Exception {
            mockMvc.perform(get("/api/books"))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Без аутентификации — доступ всегда запрещён")
    class AnonymousAccess {

        @Test
        @DisplayName("GET /api/admin/books -> 403")
        void adminBooks() throws Exception {
            mockMvc.perform(get("/api/admin/books")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/admin/books/{id} -> 403")
        void deleteBook() throws Exception {
            mockMvc.perform(delete("/api/admin/books/1")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/groups/user -> 403")
        void groupsOfUser() throws Exception {
            mockMvc.perform(get("/api/groups/user")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/groups -> 403")
        void allGroups() throws Exception {
            mockMvc.perform(get("/api/groups")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/librarian/groups -> 403")
        void librarianGroups() throws Exception {
            mockMvc.perform(get("/api/librarian/groups")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/users -> 403")
        void allUsers() throws Exception {
            mockMvc.perform(get("/api/users")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("DELETE /api/admin/users/{id} -> 403")
        void deleteUser() throws Exception {
            mockMvc.perform(delete("/api/admin/users/1")).andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("GET /api/categories -> 403")
        void categories() throws Exception {
            mockMvc.perform(get("/api/categories")).andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Роль ROLE_USER — обычный читатель")
    class UserRoleAccess {

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_USER)
        @DisplayName("Не может смотреть админский список книг")
        void cannotSeeAdminBooks() throws Exception {
            mockMvc.perform(get("/api/admin/books")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_USER)
        @DisplayName("Не может удалить книгу")
        void cannotDeleteBook() throws Exception {
            mockMvc.perform(delete("/api/admin/books/1")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_USER)
        @DisplayName("Может смотреть свои группы")
        void canSeeOwnGroups() throws Exception {
            mockMvc.perform(get("/api/groups/user")).andExpect(status().isOk());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_USER)
        @DisplayName("Не может смотреть список всех групп (админский эндпоинт)")
        void cannotSeeAllGroups() throws Exception {
            mockMvc.perform(get("/api/groups")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_USER)
        @DisplayName("Не может зайти в кабинет библиотекаря")
        void cannotAccessLibrarianGroups() throws Exception {
            mockMvc.perform(get("/api/librarian/groups")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_USER)
        @DisplayName("Не может получить список всех пользователей")
        void cannotListUsers() throws Exception {
            mockMvc.perform(get("/api/users")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_USER)
        @DisplayName("Не может удалить пользователя")
        void cannotDeleteUser() throws Exception {
            mockMvc.perform(delete("/api/admin/users/1")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_USER)
        @DisplayName("Может смотреть список категорий")
        void canlistCategories() throws Exception {
            mockMvc.perform(get("/api/categories")).andExpect(status().isOk());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_USER)
        @DisplayName("Не может создать категорию")
        void cannotCreateCategory() throws Exception {
            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CATEGORY_BODY))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("Роль ROLE_LIBRARIAN — библиотекарь")
    class LibrarianRoleAccess {

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_LIBRARIAN)
        @DisplayName("Не имеет доступа к админскому списку книг")
        void cannotSeeAdminBooks() throws Exception {
            mockMvc.perform(get("/api/admin/books")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_LIBRARIAN)
        @DisplayName("Не может удалить книгу (это админская операция)")
        void cannotDeleteBook() throws Exception {
            mockMvc.perform(delete("/api/admin/books/1")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_LIBRARIAN)
        @DisplayName("Может смотреть свои группы (как обычный авторизованный юзер)")
        void canSeeOwnGroups() throws Exception {
            mockMvc.perform(get("/api/groups/user")).andExpect(status().isOk());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_LIBRARIAN)
        @DisplayName("Не может смотреть список всех групп (это эндпоинт админа)")
        void cannotSeeAllGroups() throws Exception {
            mockMvc.perform(get("/api/groups")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_LIBRARIAN)
        @DisplayName("Может зайти в свой кабинет библиотекаря")
        void canAccessLibrarianGroups() throws Exception {
            mockMvc.perform(get("/api/librarian/groups")).andExpect(status().isOk());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_LIBRARIAN)
        @DisplayName("Может получить список пользователей")
        void canListUsers() throws Exception {
            mockMvc.perform(get("/api/users")).andExpect(status().isOk());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_LIBRARIAN)
        @DisplayName("Не может удалить пользователя (это админская операция)")
        void cannotDeleteUser() throws Exception {
            mockMvc.perform(delete("/api/admin/users/1")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_LIBRARIAN)
        @DisplayName("Может создавать категории")
        void canCreateCategory() throws Exception {
            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CATEGORY_BODY))
                    .andExpect(status().isOk());
        }
    }

    @Nested
    @DisplayName("Роль ROLE_ADMIN — администратор")
    class AdminRoleAccess {

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_ADMIN)
        @DisplayName("Может смотреть админский список книг")
        void canSeeAdminBooks() throws Exception {
            mockMvc.perform(get("/api/admin/books")).andExpect(status().isOk());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_ADMIN)
        @DisplayName("Может удалить книгу")
        void canDeleteBook() throws Exception {
            mockMvc.perform(delete("/api/admin/books/1")).andExpect(status().isOk());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_ADMIN)
        @DisplayName("Может смотреть список всех групп")
        void canSeeAllGroups() throws Exception {
            mockMvc.perform(get("/api/groups")).andExpect(status().isOk());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_ADMIN)
        @DisplayName("НЕ может зайти в кабинет библиотекаря — роли не наследуются")
        void cannotAccessLibrarianGroups() throws Exception {
            mockMvc.perform(get("/api/librarian/groups")).andExpect(status().isForbidden());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_ADMIN)
        @DisplayName("Может получить список всех пользователей")
        void canListUsers() throws Exception {
            mockMvc.perform(get("/api/users")).andExpect(status().isOk());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_ADMIN)
        @DisplayName("Может удалить пользователя")
        void canDeleteUser() throws Exception {
            mockMvc.perform(delete("/api/admin/users/1")).andExpect(status().isOk());
        }

        @Test
        @WithMockLibraryUser(role = User.ROLE.ROLE_ADMIN)
        @DisplayName("Может создавать категории")
        void canCreateCategory() throws Exception {
            mockMvc.perform(post("/api/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(CATEGORY_BODY))
                    .andExpect(status().isOk());
        }
    }
}
