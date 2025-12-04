package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookIdDTO;
import nsu.library.entity.LastRead;
import nsu.library.entity.User;
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
        User user = (User) auth.getPrincipal();
        return lastReadService.addBookToLastRead(bookId.getId(), user.getId());
    }

    @GetMapping
    public List<LastRead> getLastReadListByUser(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return lastReadService.getLastReadListByUser(user.getId());
    }

    @DeleteMapping
    public LastRead deleteBookFromLastRead(BookIdDTO bookId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        return lastReadService.deleteBookFromLastRead(bookId.getId(), user.getId());
    }
}
