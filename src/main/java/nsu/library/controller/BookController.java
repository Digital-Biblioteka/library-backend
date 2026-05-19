package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.permissions.AccessRequestDTO;
import nsu.library.entity.BookAccessRequest;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.bookpermissions.AccessControlService;
import nsu.library.service.bookpermissions.PermissionService;
import nsu.library.service.books.BookService;
import nsu.library.service.groups.GroupService;
import nsu.library.service.user.UserService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequestMapping("api/books")
@RequiredArgsConstructor
@RestController
public class BookController {
    private final PermissionService permissionService;
    private final AccessControlService accessControlService;
    private final BookService bookService;
    private final UserService userService;
    private final GroupService groupService;

    // user делает реквест из конкретной группы на его выбор
    @PostMapping("/{bookID}/access")
    @PreAuthorize("isAuthenticated()")
    public BookAccessRequest CreateAccessRequest(@PathVariable Long bookID, @RequestBody AccessRequestDTO req, @AuthenticationPrincipal CustomUserDetails user) throws IOException {
        return accessControlService.AddAccessRequest(bookID, user.getUser().getId(), req.getGroupID());
    }


    @GetMapping("/access")
    @PreAuthorize("isAuthenticated()")
    public List<BookAccessRequest> ListAccessRequestsByUser(@AuthenticationPrincipal CustomUserDetails user) {
        return accessControlService.GetAccessRequestsByUserId(user.getUser().getId());
    }
}
