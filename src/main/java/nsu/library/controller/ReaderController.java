package nsu.library.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.BookPreviewDTO;
import nsu.library.entity.Book;
import nsu.library.service.BookImport;
import nsu.library.service.BookService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/reader")
@RequiredArgsConstructor
public class ReaderController {
    BookImport bookImport;
    BookService bookService;

    @GetMapping("{id}")
    public Book getBook(@PathVariable Long id) {
        return bookService.getBook(id);
    }

    @Operation(summary = "Получение превью книги")
    @GetMapping("{id}/preview")
    public BookPreviewDTO getBookPreview(@PathVariable Long id) {
        Book book = bookService.getBook(id);
        return bookImport.getBookPreview(book.getLinkToBook());
    }
}
