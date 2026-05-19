package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.user.AddUserToGroupDTO;
import nsu.library.entity.BookAccessRequest;
import nsu.library.entity.Group;
import nsu.library.entity.User;
import nsu.library.entity.UserGroup;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.bookpermissions.AccessControlService;
import nsu.library.service.groups.GroupService;
import nsu.library.service.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/librarian")
@RequiredArgsConstructor
public class LibrarianPermissionController {
    private final AccessControlService accessControlService;
    private final GroupService groupService;
    private final UserService userService;

    //TODO: юзер делает реквест и библиотекарь его аппрувит по идее? дто нужна
    @Operation(summary = "Получить список запросов на доступ своей группы")
    @GetMapping("/requests/groups/{groupID}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public List<BookAccessRequest> ListAccessRequestsByGroup(@PathVariable String groupID, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(groupID);
        if (group == null) {
            throw new EntityNotFoundException("Group not found");
        }

        RequireLibrarianAccess(group, user);

        return accessControlService.GetAccessRequestsByGroup(groupID);
    }

    @Operation(summary = "Одобрить запрос на доступ к книге")
    @PostMapping("/requests/{requestID}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public BookAccessRequest ApproveRequest(@PathVariable String requestID, @AuthenticationPrincipal CustomUserDetails user) {
        BookAccessRequest request = accessControlService.GetAccessRequestByID(requestID);
        if  (request == null) {
            throw new EntityNotFoundException("Request not found");
        }

        RequireLibrarianAccess(request.getGroup(), user);

        request.setStatus(BookAccessRequest.RequestStatus.APPROVED);

        return request;
    }

    @Operation(summary = "Получить список пользователей в своей группе")
    @GetMapping("/groups")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<UserGroup> GetUsersInGroup(@AuthenticationPrincipal CustomUserDetails user) {
        return groupService.GetUsersByLibrarian(user.getUser().getId());
    }

    @Operation(summary = "Добавить пользователя в группу")
    @PostMapping("/groups")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public UserGroup AddUserToGroup(@RequestBody AddUserToGroupDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(req.getGroupID());
        RequireLibrarianAccess(group, user);
        User addedUser = userService.getUserByEmail(req.getEmail());
        if (addedUser == null) {
            throw new EntityNotFoundException("User not found");
        }
        return groupService.AddUserToGroup(addedUser.getId(), req.getGroupID());
    }

    @Operation(summary = "Удалить пользователя из группы")
    @DeleteMapping("groups/{userId}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    public void DeleteUserFromGroup(@PathVariable String userId, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(userId);
        RequireLibrarianAccess(group, user);

        groupService.RemoveUserFromGroup(user.getUser().getId(), group.getId());
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    void RequireLibrarianAccess(Group group, CustomUserDetails user) {
        if (!user.getUser().getId().equals(group.getLibrarian().getId())) {
            throw new AccessDeniedException("Access denied. Does not have permission to access this request");
        }
    }
}
