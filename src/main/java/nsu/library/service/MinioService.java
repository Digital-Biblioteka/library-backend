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
    String epubBucketName = "epub";

    private final MinioClient minioClient = MinioClient.builder()
            .endpoint("https://play.min.io")
            .credentials("Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG")
            .build();

    public String getUrlOfEpubBook(Long id) {
        String url = null;
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            return null;
        }
        try {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET).
                            bucket(epubBucketName).
                            object(book.getLinkToBook()).
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

    public String loadBookEpub(MultipartFile file) {
        try {
            String filePath = file.getOriginalFilename();
            if (filePath == null) {
                filePath = "defaultName.epub";
            }
            String fileName = Paths.get(filePath).toFile().getName();
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("epub")
                            .object(fileName)
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
        return file.getName();
    }
}