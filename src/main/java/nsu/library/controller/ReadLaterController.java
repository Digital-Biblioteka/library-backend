package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookIdDTO;
import nsu.library.dto.ReadLaterBookDTO;
import nsu.library.entity.ReadLater;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.books.ReadLaterService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/read-later")
public class ReadLaterController {
    private final ReadLaterService readLaterService;

    @PostMapping
    public ReadLater addBookToReadLater(BookIdDTO bookId, Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return readLaterService.addBookToReadLater(user.getUser().getId(), bookId.getId());
    }

    @DeleteMapping("{id}")
    public void deleteBookFromReadLater(@PathVariable Long id, Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        readLaterService.deleteBookFromReadLater(user.getUser().getId(), id);
    }

    @GetMapping
    public List<ReadLaterBookDTO> getReadLaterListByUser(Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return readLaterService.getListOfReadLaterBooksByUser(user.getUser().getId());
    }
}
