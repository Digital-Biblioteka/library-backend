package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.ReadingPosition;
import nsu.library.repository.ReadingPositionRepository;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.books.LastReadService;
import nsu.library.service.books.ReaderService;
import nsu.library.service.minio.MinioService;
import nsu.library.util.ReadingPositionId;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reader")
@RequiredArgsConstructor
public class ReaderController {
    private final MinioService minioService;
    private final ReaderService readerService;
    private final ReadingPositionRepository readingPositionRepository;
    private final LastReadService lastReadService;

    /**
     * Получение книжки для читалки.
     * Если пользователь залогинен в системе, то
     * автоматически добавляет книгу в список последних прочитанных для него
     * @param id ид книжки
     * @param auth сессия пользователя
     * @return ссылка на книгу в минио
     */
    @GetMapping("{id}")
    public String getBook(@PathVariable Long id, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
            lastReadService.addBookToLastRead(id, user.getUser().getId());
        }
        return minioService.getUrlOfEpubBook(id);
    }

    /**
     * Получение превью книги по иду книги
     * @param id long число. не название в минио, а примари кей в бд
     * @return bookPreviewDTO(обложка+метаданные)
     */
    @Operation(summary = "Получение превью книги")
    @GetMapping(
            value = "{id}/preview",
            produces = MediaType.IMAGE_JPEG_VALUE
    )
    public String getBookPreview(@PathVariable Long id) {
        return readerService.getBookPreview(id);
    }

    @GetMapping("{id}/pos")
    public ReadingPosition getBookReadingPosition(@PathVariable Long id, Authentication auth) {
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getUser().getId();
        ReadingPositionId posId = new ReadingPositionId(userId, id);
        return readingPositionRepository.findById(posId).orElseThrow();
    }

    @PostMapping("{id}/pos")
    public ReadingPosition postBookReadingPosition(@PathVariable Long id, Authentication auth,
                       @RequestBody String position) {

        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getUser().getId();

        ReadingPosition readingPosition = new ReadingPosition();
        readingPosition.setPosition(position);
        readingPosition.setUserId(userId);
        readingPosition.setBookId(id);
        readingPositionRepository.save(readingPosition);
        return readingPosition;
    }
}
