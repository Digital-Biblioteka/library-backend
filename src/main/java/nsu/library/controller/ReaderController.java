package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.ChapterDTO;
import nsu.library.dto.TocItemDTO;
import nsu.library.entity.ReadingPosition;
import nsu.library.repository.ReadingPositionRepository;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.books.LastReadService;
import nsu.library.service.books.ReaderService;
import nsu.library.service.minio.MinioService;
import nsu.library.util.ReadingPositionId;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @GetMapping(value = "{id}/preview")
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

    @GetMapping("{id}/toc")
    public List<TocItemDTO> getTableOfContents(@PathVariable Long id) {
        return readerService.getTableOfContents(id);
    }

    @PostMapping("{id}/toc/chapter")
    public ResponseEntity<byte[]> getHtmlChapterByTocItem(@PathVariable Long id, @RequestBody TocItemDTO tocItemDTO) {
        ChapterDTO dto = readerService.getHtmlChapterByTocItem(id, tocItemDTO);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header("X-Spine-Index", String.valueOf(dto.getSpineIdx()))
                .header("X-Total-Spines", String.valueOf(dto.getTotalSpines()))
                .header("X-Has-Next", String.valueOf(dto.isHasNext()))
                .header("X-Has-Prev", String.valueOf(dto.isHasPrev()))
                .body(dto.getHtml());
    }

    @GetMapping("/{id}/chapter/{spineIdx}")
    public ResponseEntity<byte[]> getChapter(@PathVariable Long id, @PathVariable int spineIdx) {
        ChapterDTO dto = readerService.getHtmlChapterBySpineIdx(id, spineIdx);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .header("X-Spine-Index", String.valueOf(dto.getSpineIdx()))
                .header("X-Total-Spines", String.valueOf(dto.getTotalSpines()))
                .header("X-Has-Next", String.valueOf(dto.isHasNext()))
                .header("X-Has-Prev", String.valueOf(dto.isHasPrev()))
                .body(dto.getHtml());
    }
}
