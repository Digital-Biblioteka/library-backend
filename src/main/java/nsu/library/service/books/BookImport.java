package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import nsu.library.dto.BookDTO;
import nsu.library.entity.Genre;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.lang.StringBuilder;

@Component
@RequiredArgsConstructor
public class BookImport {

    private final GenreService genreService;

    public nl.siegmann.epublib.domain.Book readEpub(MultipartFile file) throws IOException {
        EpubReader epubReader = new EpubReader();
        return epubReader.readEpub(file.getInputStream());
    }

    /**
     * парсим епаб из файлика извлекая все метаданные.
     * возможно придется поменять на ручной парсинг хмл
     *
     * @param book книжка типа епаблиб
     * @return дто книги
     * @throws IOException если не найдем книжку
     */
    public BookDTO parseEpub(nl.siegmann.epublib.domain.Book book) throws IOException {
        Metadata metadata = book.getMetadata();
        BookDTO ourBook = new BookDTO();
        ourBook.setAuthor(metadata.getAuthors().isEmpty() ? "" : metadata.getAuthors().getFirst().toString());
        ourBook.setTitle(metadata.getTitles().isEmpty() ? "" : metadata.getTitles().getFirst());
        ourBook.setDescription(metadata.getDescriptions().isEmpty() ? "" : metadata.getDescriptions().getFirst());
        ourBook.setPublisher(metadata.getPublishers().isEmpty() ? "" : metadata.getPublishers().getFirst());
        String genreName = metadata.getMetaAttribute("genre");
        System.out.println("i will kill myself");
        if (genreName != null) {
            // dont create new genre here
            Genre genre = genreService.AddGenre(metadata.getMetaAttribute("genre"));
            System.out.println(genre);
            System.out.println(genre.getId());
            ourBook.setGenreId(genre.getId());
        }
        return ourBook;
    }

    /**
     * Получение обложки книги.
     * надо тут как то все пооптимизировать. бэкендер дебил горе в семье
     *
     * @param file книжка
     * @return обложка в виде массива байт
     */
    public byte[] getBookPreview(MultipartFile file){
        Resource cover;
        BookDTO bookDTO;
        try {
            nl.siegmann.epublib.domain.Book book = readEpub(file);
            cover = book.getCoverImage();
        } catch (IOException e) {
            System.err.println("Error reading book in getBookPreview" + e.getMessage());
            return null;
        }
        cover.getSize();
        byte[] coverBytes;
        try {
            coverBytes = cover.getData();
        } catch (IOException e) {
            System.err.println("Error reading book in getBookPreview" + e.getMessage());
            throw new IllegalArgumentException();
        }
        return coverBytes;
    }

    public List<SpineReference> parseChapters(nl.siegmann.epublib.domain.Book book){
        Spine spine = book.getSpine();
        return spine.getSpineReferences();
    }

    public List<String> parseParagraphsByChapter(SpineReference spineReference) throws IOException {
        Resource resource = spineReference.getResource();
        InputStream is = resource.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        Document doc = Jsoup.parse(sb.toString());
        List<Element> elems = doc.getElementsByTag("p");
        List<String> paragraphs = new ArrayList<>();
        elems.forEach(element -> paragraphs.add(element.text()));
        return paragraphs;
    }
}