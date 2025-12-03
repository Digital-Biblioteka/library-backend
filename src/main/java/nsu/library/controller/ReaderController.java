package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookDTO;
import nsu.library.dto.BookPreviewDTO;
import nsu.library.service.books.BookImport;
import nsu.library.service.books.BookService;
import nsu.library.service.books.ReaderService;
import nsu.library.service.minio.MinioService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/reader")
@RequiredArgsConstructor
public class ReaderController {
    private final MinioService minioService;
    private final ReaderService readerService;

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


}
