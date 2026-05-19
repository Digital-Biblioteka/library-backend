package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.permissions.AccessRequestDTO;
import nsu.library.entity.BookAccessRequest;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.bookpermissions.AccessControlService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("api/books")
@RequiredArgsConstructor
@RestController
public class BookController {
    private final AccessControlService accessControlService;

    // user делает реквест из конкретной группы на его выбор
    @Operation(summary = "Отправить запрос на доступ к книге")
    @PostMapping("/{bookID}/access")
    @PreAuthorize("isAuthenticated()")
    public BookAccessRequest CreateAccessRequest(@PathVariable Long bookID, @RequestBody AccessRequestDTO req, @AuthenticationPrincipal CustomUserDetails user) {
        return accessControlService.AddAccessRequest(bookID, user.getUser().getId(), req.getGroupID());
    }

    @Operation(summary = "Посмотреть свои запросы на доступ")
    @GetMapping("/access")
    @PreAuthorize("isAuthenticated()")
    public List<BookAccessRequest> ListAccessRequestsByUser(@AuthenticationPrincipal CustomUserDetails user) {
        return accessControlService.GetAccessRequestsByUserId(user.getUser().getId());
    }
}
