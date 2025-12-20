package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.book.BookIdDTO;
import nsu.library.dto.list.LastReadBookDTO;
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

    /**
     * Добавить книжку в список последних прочитанных для юзера.
     * Автоматически вызывается при открытии книги в читалке
     *
     * @param bookId ид книжки
     * @param auth сессия юзера
     * @return созданный объект связи - книга-юзер
     */
    @PostMapping
    public LastRead addBookToLastRead(@RequestBody BookIdDTO bookId, Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return lastReadService.addBookToLastRead(bookId.getId(), user.getUser().getId());
    }

    /**
     * Получение списка последних прочитанных книг.
     *
     * @param auth сессия юзера
     * @return список дто прочитанных книг: ид юзера и книги + дто книги с метаданными
     */
    @GetMapping
    public List<LastReadBookDTO> getLastReadListByUser(Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return lastReadService.getLastReadListByUser(user.getUser().getId());
    }

    /**
     * Удалить книжку из списка прочитанных.
     *
     * @param bookId ид книжки
     * @param auth сессия юзера
     */
    @DeleteMapping("{bookId}")
    public void deleteBookFromLastRead(@PathVariable BookIdDTO bookId, Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        lastReadService.deleteBookFromLastRead(bookId.getId(), user.getUser().getId());
    }
}
