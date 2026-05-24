package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.permissions.BookLimitRequestDTO;
import nsu.library.dto.category.CategoryLimitRequestDTO;
import nsu.library.service.bookpermissions.CategoryPermissionService;
import nsu.library.dto.user.AddUserToGroupDTO;
import nsu.library.entity.*;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.bookpermissions.AccessControlService;
import nsu.library.service.bookpermissions.LimitService;
import nsu.library.service.bookpermissions.PermissionService;
import nsu.library.service.groups.GroupService;
import nsu.library.service.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/librarian")
@RequiredArgsConstructor
public class LibrarianController {
    private final AccessControlService accessControlService;
    private final GroupService groupService;
    private final UserService userService;
    private final PermissionService permissionService;
    private final LimitService limitService;
    private final CategoryPermissionService categoryPermissionService;

    // --- Access requests (user → librarian) ---

    @Operation(summary = "Получить список запросов на доступ своей группы")
    @GetMapping("/requests/groups/{groupID}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<BookAccessRequest> ListAccessRequestsByGroup(@PathVariable UUID groupID, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(groupID);
        RequireLibrarianAccess(group, user);
        return accessControlService.GetAccessRequestsByGroup(groupID);
    }

    @Operation(summary = "Одобрить запрос на доступ к книге")
    @PostMapping("/requests/{requestID}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public BookAccessRequest ApproveRequest(@PathVariable UUID requestID, @AuthenticationPrincipal CustomUserDetails user) {
        BookAccessRequest request = accessControlService.GetAccessRequestByID(requestID);
        RequireLibrarianAccess(request.getGroup(), user);
        permissionService.GiveBookPermission(request.getBook().getId(), request.getUser().getId(), request.getGroup().getId());
        request.setStatus(BookAccessRequest.RequestStatus.APPROVED);
        return accessControlService.SaveAccessRequest(request);
    }

    // --- Group management ---

    @Operation(summary = "Получить список групп библиотекаря")
    @GetMapping("/groups")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<Group> GetGroupsByLibrarian(@AuthenticationPrincipal CustomUserDetails user) {
        return groupService.getGroupsByLibrarian(user.getUser().getId());
    }

    @Operation(summary = "Получить список пользователей в заданной группе")
    @GetMapping("/groups/{id}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<UserGroup> GetUsersInGroup(@PathVariable UUID id, @AuthenticationPrincipal CustomUserDetails user) {
        return groupService.GetUsersByGroup(id);
    }

    @Operation(summary = "Добавить пользователя в группу")
    @PostMapping("/groups/{groupID}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public UserGroup AddUserToGroup(@PathVariable UUID groupID, @RequestBody AddUserToGroupDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(groupID);
        RequireLibrarianAccess(group, user);
        User addedUser = userService.getUserByEmail(req.getEmail());
        if (addedUser == null) {
            throw new EntityNotFoundException("User not found");
        }
        return groupService.AddUserToGroup(addedUser.getId(), groupID);
    }


    @Operation(summary = "Создать запрос на увеличение лимита на книгу для своей группы")
    @PostMapping("/limits/requests")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public BookLimitRequest CreateLimitRequest(@RequestBody BookLimitRequestDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(req.getGroupID());
        RequireLibrarianAccess(group, user);
        return limitService.AddLimitRequest(req.getGroupID(), req.getBookID(), req.getRequestedLimit());
    }

    @Operation(summary = "Просмотреть свои запросы на лимиты по группе")
    @GetMapping("/limits/requests/{groupID}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<BookLimitRequest> GetLimitRequestsByGroup(@PathVariable UUID groupID, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(groupID);
        RequireLibrarianAccess(group, user);
        return limitService.GetLimitRequestsByGroup(groupID);
    }

    @Operation(summary = "Просмотреть все свои запросы на лимиты (по всем группам)")
    @GetMapping("/limits/requests")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<BookLimitRequest> GetAllMyLimitRequests(@AuthenticationPrincipal CustomUserDetails user) {
        return limitService.GetLimitRequestsByLibrarian(user.getUser().getId());
    }

    // --- Category access requests (user → librarian) ---

    @Operation(summary = "Запросы пользователей на доступ к категориям группы")
    @GetMapping("/categories/requests/{groupID}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<nsu.library.entity.CategoryAccessRequest> GetCategoryAccessRequestsByGroup(
            @PathVariable UUID groupID, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(groupID);
        RequireLibrarianAccess(group, user);
        return categoryPermissionService.getAccessRequestsByGroup(groupID);
    }

    @Operation(summary = "Одобрить запрос пользователя на доступ к категории")
    @PostMapping("/categories/requests/{requestID}/approve")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public nsu.library.entity.CategoryAccessRequest ApproveCategoryAccessRequest(
            @PathVariable UUID requestID, @AuthenticationPrincipal CustomUserDetails user) {
        nsu.library.entity.CategoryAccessRequest req = categoryPermissionService.getAccessRequestById(requestID);
        RequireLibrarianAccess(req.getGroup(), user);
        return categoryPermissionService.approveAccessRequest(requestID);
    }

    @Operation(summary = "Отклонить запрос пользователя на доступ к категории")
    @PostMapping("/categories/requests/{requestID}/reject")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public nsu.library.entity.CategoryAccessRequest RejectCategoryAccessRequest(
            @PathVariable UUID requestID, @AuthenticationPrincipal CustomUserDetails user) {
        nsu.library.entity.CategoryAccessRequest req = categoryPermissionService.getAccessRequestById(requestID);
        RequireLibrarianAccess(req.getGroup(), user);
        return categoryPermissionService.rejectAccessRequest(requestID);
    }

    // --- Category limit requests (librarian → admin) ---

    @Operation(summary = "Запросить у админа лимиты на категорию для своей группы")
    @PostMapping("/categories/limits/requests")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public nsu.library.entity.CategoryLimitRequest CreateCategoryLimitRequest(
            @RequestBody CategoryLimitRequestDTO req,
            @AuthenticationPrincipal CustomUserDetails user) {
        return categoryPermissionService.createLimitRequest(
                user.getUser().getId(), req.getGroupID(), req.getCategoryID(), req.getRequestedLimit());
    }

    @Operation(summary = "Мои запросы на лимиты по категориям (все группы)")
    @GetMapping("/categories/limits/requests")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<nsu.library.entity.CategoryLimitRequest> GetMyCategoryLimitRequests(
            @AuthenticationPrincipal CustomUserDetails user) {
        return categoryPermissionService.getLimitRequestsByLibrarian(user.getUser().getId());
    }

    @Operation(summary = "Запросы на лимиты по категориям конкретной группы")
    @GetMapping("/categories/limits/requests/{groupID}")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<nsu.library.entity.CategoryLimitRequest> GetCategoryLimitRequestsByGroup(
            @PathVariable UUID groupID, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(groupID);
        RequireLibrarianAccess(group, user);
        return categoryPermissionService.getLimitRequestsByGroup(groupID);
    }

    // --- Internal helper ---

    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    void RequireLibrarianAccess(Group group, CustomUserDetails user) {
        if (!user.getUser().getId().equals(group.getLibrarian().getId())) {
            throw new AccessDeniedException("Access denied. Does not have permission to access this request");
        }
    }
}
