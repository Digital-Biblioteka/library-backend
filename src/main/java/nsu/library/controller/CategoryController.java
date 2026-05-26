package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.category.BookCategoryDTO;
import nsu.library.dto.category.CategoryAccessRequestDTO;
import nsu.library.entity.*;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.bookpermissions.BookCategoryService;
import nsu.library.service.bookpermissions.CategoryPermissionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Публичный и ролевой API для категорий книг.
 *
 * GET  /api/categories                     — список всех категорий (все авторизованные)
 * GET  /api/categories/{id}                — одна категория
 * GET  /api/categories/{id}/books          — книги в категории
 * GET  /api/categories/book/{bookId}       — категории книги
 *
 * POST   /api/categories                   — создать (librarian | admin)
 * PUT    /api/categories/{id}              — редактировать (librarian | admin)
 * DELETE /api/categories/{id}              — удалить (librarian | admin)
 *
 * POST   /api/categories/{id}/books/{bookId}    — добавить книгу в категорию (librarian | admin)
 * DELETE /api/categories/{id}/books/{bookId}    — убрать книгу из категории (librarian | admin)
 *
 * POST /api/categories/access              — пользователь запрашивает доступ к категории
 * GET  /api/categories/access/my           — пользователь смотрит свои запросы
 * GET  /api/categories/permissions/my      — пользователь смотрит активные разрешения
 */
@RestController
@RequestMapping("api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final BookCategoryService bookCategoryService;
    private final CategoryPermissionService categoryPermissionService;

    @Operation(summary = "Список всех категорий")
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public List<BookCategoryDTO> getAllCategories() {
        List<BookCategory> categories = bookCategoryService.getAllCategories();
        List<BookCategoryDTO> categoriesDTO = new ArrayList<>();
        for (BookCategory category:  categories) {
            BookCategoryDTO dto = convertToDTO(category);
            categoriesDTO.add(dto);
        }
        return categoriesDTO;
    }

    @Operation(summary = "Получить категорию по ID")
    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public BookCategoryDTO getCategoryById(@PathVariable UUID id) {
        return convertToDTO(bookCategoryService.getCategoryById(id));
    }

    @Operation(summary = "Книги в категории")
    @GetMapping("/{id}/books")
    @PreAuthorize("isAuthenticated()")
    public List<BookCategoryAssignment> getBooksInCategory(@PathVariable UUID id) {
        return bookCategoryService.getBooksInCategory(id);
    }

    @Operation(summary = "Категории конкретной книги")
    @GetMapping("/book/{bookId}")
    @PreAuthorize("isAuthenticated()")
    public List<BookCategoryAssignment> getCategoriesForBook(@PathVariable Long bookId) {
        return bookCategoryService.getCategoriesForBook(bookId);
    }

    @Operation(summary = "Создать категорию")
    @PostMapping
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public BookCategory createCategory(@RequestBody BookCategoryDTO dto,
                                       @AuthenticationPrincipal CustomUserDetails user) {
        return bookCategoryService.createCategory(dto, user.getUser().getId());
    }

    @Operation(summary = "Обновить категорию")
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public BookCategory updateCategory(@PathVariable UUID id, @RequestBody BookCategoryDTO dto) {
        return bookCategoryService.updateCategory(id, dto);
    }

    @Operation(summary = "Удалить категорию")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public void deleteCategory(@PathVariable UUID id) {
        bookCategoryService.deleteCategory(id);
    }

    // ---- Назначение книг (librarian | admin) ----

    @Operation(summary = "Добавить книгу в категорию")
    @PostMapping("/{id}/books/{bookId}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public BookCategoryAssignment assignBook(@PathVariable UUID id, @PathVariable Long bookId) {
        return bookCategoryService.assignBookToCategory(bookId, id);
    }

    @Operation(summary = "Убрать книгу из категории")
    @DeleteMapping("/{id}/books/{bookId}")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN','ROLE_ADMIN')")
    public void removeBook(@PathVariable UUID id, @PathVariable Long bookId) {
        bookCategoryService.removeBookFromCategory(bookId, id);
    }

    @Operation(summary = "Запросить доступ к категории (в рамках группы)")
    @PostMapping("/access")
    @PreAuthorize("isAuthenticated()")
    public CategoryAccessRequest requestCategoryAccess(
            @RequestBody CategoryAccessRequestDTO dto,
            @AuthenticationPrincipal CustomUserDetails user) {
        return categoryPermissionService.createAccessRequest(
                user.getUser().getId(), dto.getGroupID(), dto.getCategoryID());
    }

    @Operation(summary = "Мои запросы на доступ к категориям")
    @GetMapping("/access/my")
    @PreAuthorize("isAuthenticated()")
    public List<CategoryAccessRequest> myAccessRequests(@AuthenticationPrincipal CustomUserDetails user) {
        return categoryPermissionService.getAccessRequestsByUser(user.getUser().getId());
    }

    @Operation(summary = "Мои активные разрешения на категории")
    @GetMapping("/permissions/my")
    @PreAuthorize("isAuthenticated()")
    public List<CategoryPermission> myPermissions(@AuthenticationPrincipal CustomUserDetails user) {
        return categoryPermissionService.getUserPermissions(user.getUser().getId());
    }

    public BookCategoryDTO convertToDTO(BookCategory category) {
        return new BookCategoryDTO(category.getName(), category.getDescription());
    }
}
