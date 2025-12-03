package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nl.siegmann.epublib.domain.Resource;
import nsu.library.dto.BookPreviewDTO;
import nsu.library.repository.BookRepository;
import nsu.library.service.books.BookImport;
import nsu.library.service.books.BookService;
import nsu.library.service.minio.MinioService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequestMapping("api/books")
@RestController
@RequiredArgsConstructor
public class BookController {
    private final BookRepository bookRepository;
    private final BookService bookService;
    private final MinioService minioService;
    private final BookImport bookImport;

    @GetMapping("test")
    public String GetBook() {
        String book = minioService.getUrlOfEpubBook(1L);
        System.out.println(book);
        return book;
    }

    @PostMapping(
            value = "load",
            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String LoadBook(@RequestParam("file") MultipartFile file) {
        return minioService.loadBookEpub(file, "1");
    }

//    @PostMapping(
//            value = "getPreview",
//            consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE
//    )
//    public ResponseEntity<Resource> getCover(@RequestParam("file") MultipartFile file) {
//
//            return ResponseEntity.ok()
//                    .contentType(MediaType.IMAGE_JPEG) // или PNG
//                    .body(cover);
//        }
//    }
    @GetMapping("getPreview/{id}")
    public String getCover(@PathVariable String id) {
        return minioService.getBookCover(id);
    }
}
