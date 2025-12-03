package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookPreviewDTO;
import nsu.library.entity.ReadingPosition;
import nsu.library.entity.User;
import nsu.library.repository.ReadingPositionRepository;
import nsu.library.service.books.ReaderService;
import nsu.library.service.minio.MinioService;
import nsu.library.util.ReadingPositionId;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reader")
@RequiredArgsConstructor
public class ReaderController {
    private final MinioService minioService;
    private final ReaderService readerService;
    private final ReadingPositionRepository readingPositionRepository;

    @GetMapping("{id}")
    public String getBook(@PathVariable Long id) {
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
    public BookPreviewDTO getBookPreview(@PathVariable Long id) {
        return readerService.getBookPreview(id);
    }

    @GetMapping("{id}/pos")
    public ReadingPosition getBookReadingPosition(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Long userId = user.getId();
        ReadingPositionId posId = new ReadingPositionId(userId, id);
        return readingPositionRepository.findById(posId).orElseThrow();
    }

    @PostMapping("{id}/pos")
    public ReadingPosition postBookReadingPosition(@PathVariable Long id, @AuthenticationPrincipal User user,
                       @RequestBody String position) {
        Long userId = user.getId();

        ReadingPosition readingPosition = new ReadingPosition();
        readingPosition.setPosition(position);
        readingPosition.setUserId(userId);
        readingPosition.setBookId(id);
        readingPositionRepository.save(readingPosition);
        return readingPosition;
    }
}
