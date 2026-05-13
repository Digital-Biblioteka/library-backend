package nsu.library.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.user.AddUserToGroupDTO;
import nsu.library.entity.BookAccessRequest;
import nsu.library.entity.Group;
import nsu.library.entity.User;
import nsu.library.entity.UserGroup;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.bookpermissions.AccessControlService;
import nsu.library.service.bookpermissions.PermissionService;
import nsu.library.service.groups.GroupService;
import nsu.library.service.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("api/librarian")
@RequiredArgsConstructor
public class LibrarianPermissionController {
    private final PermissionService permissionService;
    private final AccessControlService accessControlService;
    private final UserService userService;
    private final GroupService groupService;

    //TODO: юзер делает реквест и библиотекарь его аппрувит по идее? дто нужна
    @GetMapping("requests/groups/{groupID}")
    public List<BookAccessRequest> ListAccessRequestsByGroup(@PathVariable String groupID, Authentication auth) {
        if (auth == null) {
            throw new AccessDeniedException("User not logged in");
        }
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        Group group = groupService.getGroupById(groupID);
        if (group == null) {
            throw new EntityNotFoundException("Group not found");
        }

        if (user.getUser().getRole() != User.ROLE.ROLE_LIBRARIAN) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        if (!Objects.equals(user.getUser().getId(), group.getLibrarian().getId())) {
            throw new AccessDeniedException("Access denied. Does not have permission to access this group");
        }

        return permissionService.GetAccessRequestsByGroup(groupID);
    }

    @PostMapping("/requests/{requestID}")
    public BookAccessRequest ApproveRequest(@PathVariable String requestID, Authentication auth) {
        if (auth == null) {
            throw new AccessDeniedException("User not logged in");
        }
        BookAccessRequest request = permissionService.GetAccessRequestByID(requestID);
        if  (request == null) {
            throw new EntityNotFoundException("Request not found");
        }

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        if (!user.getUser().getRole().equals(User.ROLE.ROLE_LIBRARIAN)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        if (!user.getUser().getId().equals(request.getGroup().getLibrarian().getId())) {
            throw new AccessDeniedException("Access denied. Does not have permission to access this request");
        }

        request.setStatus(BookAccessRequest.RequestStatus.APPROVED);

        return request;
    }

    @PostMapping("/groups/users")
    public UserGroup AddUserToGroup(@RequestBody AddUserToGroupDTO req, Authentication auth) {
        if (auth == null) {
            throw new AccessDeniedException("User not logged in");
        }

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();

        if (!user.getUser().getRole().equals(User.ROLE.ROLE_LIBRARIAN)) {
            throw new AccessDeniedException("You do not have permission to access this resource");
        }

        Group group = groupService.getGroupById(req.getGroupID());
        if (!user.getUser().getId().equals(group.getLibrarian().getId())) {
            throw new AccessDeniedException("Access denied. Does not have permission to access this request");
        }

        return groupService.AddUserToGroup(req.getUserID(), req.getGroupID());
    }


}
