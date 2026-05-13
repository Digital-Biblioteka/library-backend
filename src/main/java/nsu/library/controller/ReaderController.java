package nsu.library.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.reader.ChapterDTO;
import nsu.library.dto.reader.TocItemDTO;
import nsu.library.exception.MinioErrorException;
import nsu.library.entity.ReadingPosition;
import nsu.library.repository.ReadingPositionRepository;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.books.LastReadService;
import nsu.library.service.books.ReaderService;
import nsu.library.service.minio.MinioService;
import nsu.library.util.ReadingPositionId;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("api/reader")
@RequiredArgsConstructor
public class ReaderController {
    private final MinioService minioService;
    private final ReaderService readerService;
    private final ReadingPositionRepository readingPositionRepository;
    private final LastReadService lastReadService;
    private final ObjectMapper objectMapper;

    private record ReadingPosDTO(Integer spineIdx) {}

    private ResponseEntity<ChapterDTO> toChapterResponse(ChapterDTO dto) {
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

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
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/reader/")
                .path(String.valueOf(id))
                .path("/file")
                .toUriString();
    }

    @GetMapping(value = "{id}/file", produces = "application/epub+zip")
    public ResponseEntity<InputStreamResource> downloadBook(@PathVariable Long id) {
        String bookLink = readerService.getBookLink(id);
        InputStreamResource res = new InputStreamResource(minioService.getRealBook(bookLink));
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/epub+zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + bookLink + "\"")
                .body(res);
    }

    /**
     * Получение превью книги по иду книги
     * @param id long число. не название в минио, а примари кей в бд
     * @return bookPreviewDTO(обложка+метаданные)
     */
    @Operation(summary = "Получение превью книги")
    @GetMapping(value = "{id}/preview")
    public String getBookPreview(@PathVariable Long id) {
        return ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/reader/")
                .path(String.valueOf(id))
                .path("/preview/image")
                .toUriString();
    }

    @GetMapping(value = "{id}/preview/image", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<InputStreamResource> getBookPreviewImage(@PathVariable Long id) {
        String bookLink = readerService.getBookPreview(id);
        try {
            InputStreamResource res = new InputStreamResource(minioService.getRealCover(bookLink));
            return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(res);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("{id}/pos")
    public ResponseEntity<String> getBookReadingPosition(@PathVariable Long id, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        Long userId = user.getUser().getId();
        ReadingPositionId posId = new ReadingPositionId(userId, id);

        ReadingPosition pos = readingPositionRepository.findById(posId).orElse(null);
        if (pos == null || pos.getPosition() == null || pos.getPosition().isBlank()) {
            return ResponseEntity.ok("null");
        }

        Integer spineIdx = null;
        try {
            JsonNode node = objectMapper.readTree(pos.getPosition());
            JsonNode idxNode = node != null ? node.get("spineIdx") : null;
            if (idxNode != null && idxNode.isNumber()) {
                spineIdx = idxNode.intValue();
            }
        } catch (Exception ignored) {
        }

        try {
            return ResponseEntity.ok(objectMapper.writeValueAsString(new ReadingPosDTO(spineIdx)));
        } catch (Exception e) {
            return ResponseEntity.ok("null");
        }
    }

    @PostMapping("{id}/pos")
    public ReadingPosition postBookReadingPosition(@PathVariable Long id, Authentication auth,
                       @RequestBody String position) {

        if (auth == null || !auth.isAuthenticated()) {
            throw new org.springframework.security.access.AccessDeniedException("Unauthorized");
        }

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
    public ResponseEntity<ChapterDTO> getHtmlChapterByTocItem(@PathVariable Long id, @RequestBody TocItemDTO tocItemDTO) {
        ChapterDTO dto = readerService.getHtmlChapterByTocItem(id, tocItemDTO);
        return toChapterResponse(dto);
    }

    @Operation(summary = "Get a chapter of book by spine index")
    @GetMapping("/{id}/chapter/{spineIdx}")
    public ResponseEntity<ChapterDTO> getChapter(@PathVariable Long id, @PathVariable int spineIdx) {
        ChapterDTO dto = readerService.getHtmlChapterBySpineIdx(id, spineIdx);
        return toChapterResponse(dto);
    }
}
