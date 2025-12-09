package nsu.library.service.storage;

import org.springframework.web.multipart.MultipartFile;

public interface Storage {
    String getBook(String bookName);
    String addBook(MultipartFile file, String bookName);
}
