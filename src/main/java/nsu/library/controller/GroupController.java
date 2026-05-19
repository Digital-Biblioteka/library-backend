package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.group.BookLimitDTO;
import nsu.library.dto.group.EditBookLimitDTO;
import nsu.library.dto.group.GroupDTO;
import nsu.library.dto.user.AddUserToGroupDTO;
import nsu.library.entity.BookLimit;
import nsu.library.entity.Group;
import nsu.library.entity.User;
import nsu.library.entity.UserGroup;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.bookpermissions.LimitService;
import nsu.library.service.groups.GroupService;
import nsu.library.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/groups")
@RequiredArgsConstructor
@RestController
public class GroupController {
    private final GroupService groupService;
    private final UserService userService;
    private final LimitService limitService;

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
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<Group> GetGroups(@AuthenticationPrincipal CustomUserDetails user) {
        return groupService.getAllGroups();
    }

    @Operation(summary = "Добавить пользователя в группу")
    @PostMapping("/groups")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public UserGroup AddUserToGroup(@RequestBody AddUserToGroupDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(req.getGroupID());
        User addedUser = userService.getUserByEmail(req.getEmail());
        if (addedUser == null) {
            throw new EntityNotFoundException("User not found");
        }
        return groupService.AddUserToGroup(addedUser.getId(), req.getGroupID());
    }

    @Operation(summary = "Удалить пользователя из группы")
    @DeleteMapping("groups/{userId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public void DeleteUserFromGroup(@PathVariable String userId, @AuthenticationPrincipal CustomUserDetails user) {
        Group group = groupService.getGroupById(userId);

        groupService.RemoveUserFromGroup(user.getUser().getId(), group.getId());
    }

    @PutMapping("/{groupID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Group UpdateGroup(@PathVariable String groupID, @RequestBody GroupDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        return groupService.updateGroup(groupID, req);
    }

    @PostMapping("{groupID}/books/limits")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookLimit GiveGroupBooklimit(@PathVariable String groupID, @RequestBody BookLimitDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        return limitService.AddBookLimit(groupID, req.getBookID(), req.getLimit());
    }

    @PutMapping("/books/limits/{bookLimitID}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public BookLimit EditGroupBooklimit(@PathVariable Long bookLimitID, @RequestBody EditBookLimitDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        return limitService.EditBookLimit(bookLimitID, req.getLimit());
    }

    @GetMapping("{groupID}/books/limits")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BookLimit> GetGroupBookLimits(@PathVariable String groupID) {
        return limitService.GetBookLimitsForGroup(groupID);
    }

    @GetMapping("/books/limits")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<BookLimit> GetBookLimits() {
        return limitService.GetBookLimits();
    }
}
