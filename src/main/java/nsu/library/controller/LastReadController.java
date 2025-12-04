package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookIdDTO;
import nsu.library.dto.LastReadBookDTO;
import nsu.library.entity.LastRead;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.books.LastReadService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/last-read")
public class LastReadController {
    private final LastReadService lastReadService;

    @PostMapping
    public LastRead addBookToLastRead(BookIdDTO bookId, Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return lastReadService.addBookToLastRead(bookId.getId(), user.getUser().getId());
    }

    @GetMapping
    public List<LastReadBookDTO> getLastReadListByUser(Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return lastReadService.getLastReadListByUser(user.getUser().getId());
    }

    @DeleteMapping
    public void deleteBookFromLastRead(BookIdDTO bookId, Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        lastReadService.deleteBookFromLastRead(bookId.getId(), user.getUser().getId());
    }
}
