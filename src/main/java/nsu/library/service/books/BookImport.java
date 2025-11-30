package nsu.library.service.books;

import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import nsu.library.dto.BookDTO;
import nsu.library.dto.BookPreviewDTO;
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
public class BookImport {

    public nl.siegmann.epublib.domain.Book readEpub(MultipartFile file) throws IOException {
        EpubReader epubReader = new EpubReader();
        return epubReader.readEpub(file.getInputStream());
    }

    public BookDTO parseEpub(nl.siegmann.epublib.domain.Book book, String link) throws IOException {
        Metadata metadata = book.getMetadata();
        BookDTO ourBook = new BookDTO();
        ourBook.setAuthor(metadata.getAuthors().isEmpty() ? "" : metadata.getAuthors().getFirst().toString());
        ourBook.setTitle(metadata.getTitles().isEmpty() ? "" : metadata.getTitles().getFirst());
        ourBook.setDescription(metadata.getDescriptions().isEmpty() ? "" : metadata.getDescriptions().getFirst());
        ourBook.setPublisher(metadata.getPublishers().isEmpty() ? "" : metadata.getPublishers().getFirst());
        ourBook.setGenre(metadata.getMetaAttribute("genre"));
        ourBook.setIsbn(metadata.getMetaAttribute("isbn"));

        return ourBook;
    }

    public BookPreviewDTO getBookPreview(MultipartFile file){
        Resource cover;
        BookDTO bookDTO;
        try {
            nl.siegmann.epublib.domain.Book book = readEpub(file);
            cover = book.getCoverImage();
            bookDTO = parseEpub(book, file.getOriginalFilename());
        } catch (IOException e) {
            System.err.println("Error reading book in getBookPreview" + e.getMessage());
            return null;
        }

        return new BookPreviewDTO(cover, bookDTO);
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