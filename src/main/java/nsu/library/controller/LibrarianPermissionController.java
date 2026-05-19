package nsu.library.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.user.AddUserToGroupDTO;
import nsu.library.entity.BookAccessRequest;
import nsu.library.entity.Group;
import nsu.library.entity.UserGroup;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.bookpermissions.AccessControlService;
import nsu.library.service.groups.GroupService;
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

    //TODO: юзер делает реквест и библиотекарь его аппрувит по идее? дто нужна
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

    @GetMapping("/groups")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public List<UserGroup> GetUsersInGroup(@AuthenticationPrincipal CustomUserDetails user) {
        return groupService.GetUsersByLibrarian(user.getUser().getId());
    }

    @PostMapping("/groups")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public UserGroup AddUserToGroup(@RequestBody AddUserToGroupDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(req.getGroupID());
        RequireLibrarianAccess(group, user);

        return groupService.AddUserToGroup(req.getUserID(), req.getGroupID());
    }

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
