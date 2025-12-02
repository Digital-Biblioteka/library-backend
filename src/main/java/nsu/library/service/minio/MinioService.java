package nsu.library.service.minio;

import io.minio.*;
import io.minio.errors.MinioException;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import nsu.library.repository.BookRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
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
    String coverBucketName = "cover";

    private final MinioClient minioClient;

    public String getUrlOfEpubBook(Long id) {
        String url = null;
        String linkToBook;
        Book book = bookRepository.findById(id).orElse(null);
        if (book == null) {
            linkToBook = "hitman.epub";
        } else {
            linkToBook = book.getLinkToBook();
        }
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

    public String loadBookCover(byte[] cover, String bookId) {
        String imageName = bookId + ".jpg";
        try {
            minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(coverBucketName)
                        .object(imageName)
                        .stream(new ByteArrayInputStream(cover), cover.length, -1)
                        .contentType("image/jpeg")
                        .build());
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
            throw new AccessDeniedException("minio access denied");
        } catch (IOException e) {
            System.out.println("Error occurred: " + e);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return imageName;
    }

    public String getBookCover(String bookId) {
        String url;
        String imageName = bookId + ".jpg";
        try {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET).
                            bucket(epubBucketName).
                            object(imageName).
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
}