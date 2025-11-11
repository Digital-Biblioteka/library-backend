package nsu.library.service;

import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import nsu.library.dto.BookDTO;
import nsu.library.dto.BookPreviewDTO;
import nsu.library.entity.Book;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.xml.namespace.QName;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.StringBuilder;

@Component
public class BookImport {

    public nl.siegmann.epublib.domain.Book readEpub(String fileName) throws IOException {
        EpubReader epubReader = new EpubReader();
        return epubReader.readEpub(new FileInputStream(fileName));
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
        ourBook.setLinkToBook(link);

        return ourBook;
    }

    public BookPreviewDTO getBookPreview(String bookLink){
        Resource cover;
        BookDTO bookDTO;
        try {
            nl.siegmann.epublib.domain.Book book = readEpub(bookLink);
            cover = book.getCoverImage();
            bookDTO = parseEpub(book, bookLink);
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