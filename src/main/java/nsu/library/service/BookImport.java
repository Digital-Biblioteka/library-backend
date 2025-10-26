package nsu.library.service;

import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import nsu.library.entity.Book;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import javax.xml.namespace.QName;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.lang.StringBuilder;

public class BookImport {

    public nl.siegmann.epublib.domain.Book readEpub(String fileName) throws IOException {
        EpubReader epubReader = new EpubReader();
        return epubReader.readEpub(new FileInputStream(fileName));
    }
    public Book parseEpub(nl.siegmann.epublib.domain.Book book){
        Metadata metadata = book.getMetadata();
        Book ourBook = new Book();
        ourBook.setAuthor(metadata.getAuthors().isEmpty() ? "" : metadata.getAuthors().getFirst().toString());
        ourBook.setTitle(metadata.getTitles().isEmpty() ? "" : metadata.getTitles().getFirst());
        ourBook.setDescription(metadata.getDescriptions().isEmpty() ? "" : metadata.getDescriptions().getFirst());
        ourBook.setPublisher(metadata.getPublishers().isEmpty() ? "" : metadata.getPublishers().getFirst());
        ourBook.setGenres(metadata.getMetaAttribute("genre"));
        ourBook.setIsbn(metadata.getMetaAttribute("isbn"));
        return ourBook;
    }

    public void getTableOfContents(nl.siegmann.epublib.domain.Book book) {
        book.getTableOfContents().getTocReferences()
                .forEach(reference -> System.out.println(reference.getTitle() + " " + reference.getResource().getHref()));
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

    public static void main(String[] args) throws Exception {
        nl.siegmann.epublib.domain.Book book = new BookImport().readEpub("hitman.epub");
    }
}