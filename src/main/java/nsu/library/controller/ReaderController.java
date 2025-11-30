package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookPreviewDTO;
import nsu.library.service.books.BookService;
import nsu.library.service.minio.MinioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reader")
@RequiredArgsConstructor
public class ReaderController {
    private final BookService bookService;
    private final MinioService minioService;

    @GetMapping("{id}")
    public String getBook(@PathVariable Long id) {
        String bookUrl = minioService.getUrlOfEpubBook(id);
        return bookUrl;
    }

    @Operation(summary = "Получение превью книги")
    @GetMapping("{id}/preview")
    public BookPreviewDTO getBookPreview(@PathVariable Long id) {
//        Book book = bookService.getBook(id);
//        return bookImport.getBookPreview(book.getLinkToBook());
        return null;
    }
}
