package nsu.library.service.storage;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import nsu.library.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

@Service("MinioService")
@RequiredArgsConstructor
public class MinioService implements Storage {

    private final BookRepository bookRepository;
    String epubBucketName = "raw";

    private final MinioClient minioClient;

    public String getBook(String bookName) {
        String url = null;
        try {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET).
                            bucket(epubBucketName).
                            object(bookName).
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

    public String addBook(MultipartFile file, String bookName) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(epubBucketName)
                            .object(bookName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
        } catch (IOException e) {
            System.out.println("Error occurred: " + e);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return bookName;
    }
}