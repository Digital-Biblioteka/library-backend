package nsu.library.service;

import nl.siegmann.epublib.domain.Author;
import nl.siegmann.epublib.domain.Metadata;
import nl.siegmann.epublib.epub.EpubReader;
import nsu.library.entity.Book;
import javax.xml.namespace.QName;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;


public class BookImport {

    public Book parseEpub(String fileName) throws IOException {
        EpubReader epubReader = new EpubReader();
        nl.siegmann.epublib.domain.Book book = epubReader.readEpub(new FileInputStream(fileName));

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

    public static void main(String[] args) throws Exception {
        Book book = new BookImport().parseEpub("hitman.epub");
        System.out.println(book);
    }
}