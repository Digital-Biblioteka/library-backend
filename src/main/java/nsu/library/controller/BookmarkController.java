package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.book.BookmarkDTO;
import nsu.library.entity.Bookmark;
import nsu.library.entity.BookmarkGroup;
import nsu.library.entity.BookmarkGroupUser;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.bookmarks.BookmarkGroupService;
import nsu.library.service.bookmarks.BookmarkService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;
    private final BookmarkGroupService bookmarkGroupService;

    /**
     * Create new bookmark for a book. Optionally attach to a group via dto.groupID.
     */
    @PostMapping("/{bookId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Добавить заметку")
    public Bookmark addBookmark(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long bookId,  @RequestBody BookmarkDTO dto) {
        return bookmarkService.addBookmark(dto, bookId, user.getUser().getId());
    }

    /**
     * Get personal bookmarks of the authenticated user for a specific book.
     */
    @GetMapping("/{bookId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить все заметки пользователя в заданной книге")
    public List<Bookmark> getBookmarksByUser(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long bookId) {
        return bookmarkService.getUserBookmarksByBook(user.getUser().getId(), bookId);
    }

    /**
     * Edit bookmark text / position.
     */
    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Редактировать заметку")
    public Bookmark editBookmark(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id, @RequestBody BookmarkDTO dto) {
        return bookmarkService.editBookmark(id, user.getUser().getId(), dto);
    }

    /**
     * Delete a bookmark (ownership enforced inside service).
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Удалить заметку")
    public void deleteBookmark(@AuthenticationPrincipal CustomUserDetails user, @PathVariable Long id) {
        bookmarkService.deleteBookmark(id, user.getUser().getId());
    }

    /**
     * Move an existing bookmark into a shared group.
     */
    @PutMapping("/{id}/group/{groupId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Добавить заметку в группу")
    public Bookmark addBookmarkToGroup(@AuthenticationPrincipal CustomUserDetails user,
                                       @PathVariable Long id,
                                       @PathVariable UUID groupId) {
        return bookmarkService.AddBookmarkToGroup(id, groupId, user.getUser().getId());
    }

    /**
     * Get all bookmarks by a shared group.
     * group belongs only to one book
     */
    @GetMapping("/groups/{groupId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить все заметки заданной группы. Группа жестко привязана к одной книге")
    public List<Bookmark> getBookmarksByGroup(@PathVariable UUID groupId) {
        return bookmarkService.getBookmarksByGroup(groupId);
    }

    /**
     * Create a new shared bookmark group for a book.
     * Query params: name, visibility (PRIVATE | BY_LINK)
     */
    @PostMapping("/groups/{bookId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Создать группу заметок")
    public BookmarkGroup createBookmarkGroup(@AuthenticationPrincipal CustomUserDetails user,
                                             @PathVariable Long bookId,
                                             @RequestParam String name,
                                             @RequestParam BookmarkGroup.BookmarkVisibility visibility) {
        return bookmarkGroupService.createBookmarkGroup(bookId, user.getUser(), name, visibility);
    }

    /**
     * Delete a bookmark group.
     */
    @DeleteMapping("/groups/{groupId}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Удалить группу заметок")
    public void deleteBookmarkGroup(@PathVariable UUID groupId) {
        bookmarkGroupService.deleteBookmarkGroup(groupId);
    }

    /**
     * Join a bookmark group using its access token.
     */
    @PostMapping("/groups/join/{accessToken}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Присоединиться к общей группе заметок по ссылке(токену)")
    public BookmarkGroupUser joinBookmarkGroup(@AuthenticationPrincipal CustomUserDetails user, @PathVariable UUID accessToken) {
        return bookmarkGroupService.giveAccessToBookmarkGroup(accessToken, user.getUser());
    }

    /**
     * List all members of a bookmark group.
     */
    @GetMapping("/groups/{groupId}/members")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Получить всех юзеров в общей группе заметок")
    public List<BookmarkGroupUser> getGroupMembers(@PathVariable UUID groupId) {
        return bookmarkGroupService.getUsersByBookmarkGroup(groupId);
    }
}
