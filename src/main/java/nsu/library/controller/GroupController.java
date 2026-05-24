package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.group.BookLimitDTO;
import nsu.library.dto.group.EditBookLimitDTO;
import nsu.library.dto.group.GroupDTO;
import nsu.library.dto.user.AddUserToGroupDTO;
import nsu.library.entity.*;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.bookpermissions.LimitService;
import nsu.library.service.bookpermissions.CategoryPermissionService;
import nsu.library.service.groups.GroupService;
import nsu.library.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/groups")
@RequiredArgsConstructor
@RestController
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;
    private final LimitService limitService;
    private final CategoryPermissionService categoryPermissionService;

    // --- Groups ---

    @Operation(summary = "Получить группы, в которые входит пользователь")
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public List<Group> GetGroupsByUser(@AuthenticationPrincipal CustomUserDetails user) {
        return groupService.getGroupsByUser(user.getUser());
    }

    @Operation(summary = "Создать группу")
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Group CreateGroup(@AuthenticationPrincipal CustomUserDetails user, @RequestBody GroupDTO req) {
        return groupService.createGroup(req.getLibrarianID(), req.getName(), req.getDescription());
    }

    @Operation(summary = "Получить все группы")
    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Group> GetGroups(@AuthenticationPrincipal CustomUserDetails user) {
        return groupService.getAllGroups();
    }

    @Operation(summary = "Добавить пользователя в группу")
    @PostMapping("{groupID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserGroup AddUserToGroup(@PathVariable UUID groupID, @RequestBody AddUserToGroupDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(groupID);
        User addedUser = userService.getUserByEmail(req.getEmail());
        if (addedUser == null) {
            throw new EntityNotFoundException("User not found");
        }
        return groupService.AddUserToGroup(addedUser.getId(), groupID);
    }

    @PutMapping("/{groupID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Group UpdateGroup(@PathVariable UUID groupID, @RequestBody GroupDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        return groupService.updateGroup(groupID, req);
    }

    // --- Book limits ---

    @PostMapping("{groupID}/books/limits")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookLimit GiveGroupBooklimit(@PathVariable UUID groupID, @RequestBody BookLimitDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        return limitService.AddBookLimit(groupID, req.getBookID(), req.getLimit());
    }

    @PutMapping("/books/limits/{bookLimitID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookLimit EditGroupBooklimit(@PathVariable Long bookLimitID, @RequestBody EditBookLimitDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        return limitService.EditBookLimit(bookLimitID, req.getLimit());
    }

    @GetMapping("{groupID}/books/limits")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BookLimit> GetGroupBookLimits(@PathVariable UUID groupID) {
        return limitService.GetBookLimitsForGroup(groupID);
    }

    @GetMapping("/books/limits")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BookLimit> GetBookLimits() {
        return limitService.GetBookLimits();
    }

    // --- Limit requests (librarian → admin) ---

    @Operation(summary = "Получить все запросы библиотекарей на увеличение лимитов")
    @GetMapping("/limits/requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BookLimitRequest> GetAllLimitRequests() {
        return limitService.GetAllLimitRequests();
    }

    @Operation(summary = "Одобрить запрос на увеличение лимита")
    @PostMapping("/limits/requests/{requestID}/approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookLimitRequest ApproveLimitRequest(@PathVariable UUID requestID) {
        return limitService.ApproveLimitRequest(requestID);
    }

    @Operation(summary = "Отклонить запрос на увеличение лимита")
    @PostMapping("/limits/requests/{requestID}/reject")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookLimitRequest RejectLimitRequest(@PathVariable UUID requestID) {
        return limitService.RejectLimitRequest(requestID);
    }
    // --- Category limit requests: admin ---

    @io.swagger.v3.oas.annotations.Operation(summary = "Все запросы библиотекарей на лимиты по категориям")
    @GetMapping("/categories/limits/requests")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<nsu.library.entity.CategoryLimitRequest> GetAllCategoryLimitRequests() {
        return categoryPermissionService.getAllLimitRequests();
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Одобрить запрос на лимиты категории")
    @PostMapping("/categories/limits/requests/{requestID}/approve")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public nsu.library.entity.CategoryLimitRequest ApproveCategoryLimitRequest(@PathVariable UUID requestID) {
        return categoryPermissionService.approveLimitRequest(requestID);
    }

    @io.swagger.v3.oas.annotations.Operation(summary = "Отклонить запрос на лимиты категории")
    @PostMapping("/categories/limits/requests/{requestID}/reject")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public nsu.library.entity.CategoryLimitRequest RejectCategoryLimitRequest(@PathVariable UUID requestID) {
        return categoryPermissionService.rejectLimitRequest(requestID);
    }
}
