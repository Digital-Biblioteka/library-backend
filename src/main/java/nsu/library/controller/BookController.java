package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nl.siegmann.epublib.domain.Book;
import nsu.library.dto.TocItemDTO;
import nsu.library.service.books.BookImport;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("test")
@RequiredArgsConstructor
@RestController
public class BookController {
    private final BookImport bookImport;


    @PostMapping(value = "/toc",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<TocItemDTO> test(@RequestPart("file") MultipartFile file) throws IOException {
        Book book = bookImport.readEpub(file);
        return bookImport.GetTableOfContents(book);
    }
}
