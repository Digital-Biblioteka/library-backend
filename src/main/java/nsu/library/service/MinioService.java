package nsu.library.service;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class MinioService {

    private final BookRepository bookRepository;
    String epubBucketName = "raw";

    private final MinioClient minioClient;

    public String getUrlOfEpubBook(Long id) {
        String url = null;
        //Book book = bookRepository.findById(id).orElse(null);
//        if (book == null) {
//            book = "hitman.epub";
//        }
        String linkToBook = "hitman.epub";
        try {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET).
                            bucket(epubBucketName).
                            object(linkToBook).
                            expiry(1, TimeUnit.DAYS).
                            build()
            );
        } catch (Exception e) {
            String errorMsg = "minio access error" + e.getMessage();
            System.err.println(errorMsg);
            return errorMsg;
        }
        return url;
    }

    public String loadBookEpub(MultipartFile file, String bookId) {
        String fileName = file.getOriginalFilename();
        try {
            String filePath = file.getOriginalFilename();
            if (filePath == null) {
                filePath = "defaultName.epub";
            }
            fileName = Paths.get(filePath).toFile().getName();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(epubBucketName)
                            .object(bookId)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            System.out.println(fileName + "added to bucket");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        } catch (IOException e) {
            System.out.println("Error occurred: " + e);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return fileName;
    }

}