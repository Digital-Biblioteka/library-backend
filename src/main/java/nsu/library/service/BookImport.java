package nsu.library.service;

import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import nsu.library.entity.Book;
import javax.xml.namespace.QName;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.lang.StringBuilder;

public class BookImport {

    public nl.siegmann.epublib.domain.Book readEpub(String fileName) throws IOException {
        EpubReader epubReader = new EpubReader();
        return epubReader.readEpub(new FileInputStream(fileName));
    }
    public Book parseEpub(nl.siegmann.epublib.domain.Book book) throws IOException {
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

    public List<SpineReference> getChapters(nl.siegmann.epublib.domain.Book book) throws IOException {
        Spine spine = book.getSpine();
        List<SpineReference> spineReferences = spine.getSpineReferences();
        spineReferences.getFirst();
        StringBuilder sb = new StringBuilder();
        String line;
        for (SpineReference spineReference : spineReferences) {
            Resource resource = spineReference.getResource();
            InputStream is = resource.getInputStream();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return spineReferences;
    }

    public static void main(String[] args) throws Exception {
        nl.siegmann.epublib.domain.Book book = new BookImport().readEpub("hitman.epub");
        Book bookOurs = new BookImport().parseEpub(book);
        new BookImport().getTableOfContents(book);
        new BookImport().getChapters(book);
    }
}