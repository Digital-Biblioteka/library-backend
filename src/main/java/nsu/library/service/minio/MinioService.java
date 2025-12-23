package nsu.library.service.minio;

import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import nsu.library.exception.MinioErrorException;
import nsu.library.repository.BookRepository;
import org.apache.commons.collections4.Get;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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

    /**
     * Получаем ссылку на книгу в минио.
     * Ссылку можно использовать в хттп запросах для получения книги
     *
     * @param id ид книжки(как в бд)
     * @return ссылка
     */
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

    /**
     * Загрузка книги в минио.
     * bookLink = название книги в минио = linkToBook в объекте книги = название книги + random uuid
     * todo: проверять наличие книги в бакете, возможно для этого по другому составлять ид книги в минио
     *
     * @param file файл книжки
     * @param bookLink описано выше
     * @return чета странное он возвращает, я хз зачем я это сделал
     */
    public String loadBookEpub(MultipartFile file, String bookLink) {
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
                            .object(bookLink)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build());
            System.out.println(fileName + "added to bucket");
        } catch (MinioException e) {
            System.out.println("Error occurred: " + e);
            System.out.println("HTTP trace: " + e.httpTrace());
            throw new MinioErrorException(e.getMessage());
        } catch (IOException e) {
            System.out.println("Error occurred: " + e);
            throw new MinioErrorException(e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new MinioErrorException(e.getMessage());
        }
        return fileName;
    }

    /**
     * Загрузка обложки в минио
     *
     * @param cover сама обложка
     * @param bookId минио-ссылка на книгу, чья эта обложка
     * @return название обложки в минио
     */
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
            throw new MinioErrorException(e.getMessage());
        } catch (IOException e) {
            System.out.println("Error occurred: " + e);
            throw new MinioErrorException(e.getMessage());
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new MinioErrorException(e.getMessage());
        }
        return imageName;
    }

    /**
     * Получение ссылки на обложку в минио.
     * Та же история, что с книгой, ссылку можно юзать для фетча
     *
     * @param bookLink ссылка на книжку в минио
     * @return ссылка на обложку
     */
    public String getBookCover(String bookLink) {
        String url;
        String imageName = bookLink + ".jpg";
        try {
            url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET).
                            bucket(coverBucketName).
                            object(imageName).
                            expiry(1, TimeUnit.DAYS).
                            build()
            );
        } catch (Exception e) {
            String errorMsg = "minio access error" + e.getMessage();
            System.err.println(errorMsg);
            throw new MinioErrorException(e.getMessage());
        }
        return url;
    }

    public InputStream getRealBook(String bookLink) {
        GetObjectResponse obj = null;
        try {
            obj = minioClient.getObject(
                    GetObjectArgs.builder().bucket(epubBucketName).object(bookLink).build()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return obj;
    }
}