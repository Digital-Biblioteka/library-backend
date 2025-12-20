package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import nsu.library.dto.book.BookDTO;
import nsu.library.dto.reader.BookWrapper;
import nsu.library.dto.reader.TocItemDTO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.lang.StringBuilder;
import java.util.Map;

// уххх... зря ты сюда полез...
@Component
@RequiredArgsConstructor
public class BookImport {

    //private final GenreService genreService;

    public nl.siegmann.epublib.domain.Book readEpub(MultipartFile file) throws IOException {
        EpubReader epubReader = new EpubReader();
        return epubReader.readEpub(file.getInputStream());
    }

    public nl.siegmann.epublib.domain.Book readEpubFromStream(InputStream in) throws IOException {
        EpubReader epubReader = new EpubReader();
        return epubReader.readEpub(in);
    }

    public nl.siegmann.epublib.domain.Book readEpubFile(String filePath) throws IOException {
        File initialFile = new File(filePath);
        InputStream targetStream = new FileInputStream(initialFile);

        EpubReader epubReader = new EpubReader();
        return epubReader.readEpub(targetStream);
    }

    /**
     * парсим епаб из файлика извлекая все метаданные.
     * возможно придется поменять на ручной парсинг хмл
     *
     * @param book книжка типа епаблиб
     * @return дто книги
     */
    public BookDTO parseEpub(nl.siegmann.epublib.domain.Book book){
        Metadata metadata = book.getMetadata();
        BookDTO ourBook = new BookDTO();
        ourBook.setAuthor(metadata.getAuthors().isEmpty() ? "" : metadata.getAuthors().getFirst().toString());
        ourBook.setTitle(metadata.getTitles().isEmpty() ? "" : metadata.getTitles().getFirst());
        ourBook.setDescription(metadata.getDescriptions().isEmpty() ? "" : metadata.getDescriptions().getFirst());
        ourBook.setPublisher(metadata.getPublishers().isEmpty() ? "" : metadata.getPublishers().getFirst());
        String genreName = metadata.getMetaAttribute("genre");
        System.out.println("i will kill myself");
//        if (genreName != null) {
//            // dont create new genre here
//            Genre genre = genreService.AddGenre(metadata.getMetaAttribute("genre"));
//            System.out.println(genre);
//            System.out.println(genre.getId());
//            ourBook.setGenreId(genre.getId());
//        }
        ourBook.setIsbn(metadata.getMetaAttribute("isbn"));

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

    public TocItemDTO parseTocToDTO(nl.siegmann.epublib.domain.TOCReference tocReference){
        TocItemDTO dto = new TocItemDTO();
        dto.setTitle(tocReference.getTitle());
        dto.setHtmlHref(tocReference.getResource().getHref());
        return dto;
    }

    public List<TocItemDTO> GetTableOfContents(nl.siegmann.epublib.domain.Book book) {
        List<TocItemDTO> tocReferences = new ArrayList<>();
        for (TOCReference ref :  book.getTableOfContents().getTocReferences()) {
            TocItemDTO dto = ParseTocChildren(ref);
            tocReferences.add(dto);
        }
        return tocReferences;
    }

    public TocItemDTO ParseTocChildren(TOCReference tocReference) {
        TocItemDTO dto = parseTocToDTO(tocReference);
        List<TocItemDTO> childrenDTO = new ArrayList<>();
        for (TOCReference ref: tocReference.getChildren()) {
            TocItemDTO childDTO = parseTocToDTO(ref);
            ParseTocChildren(ref);
            childrenDTO.add(childDTO);
        }
        dto.setChildren(childrenDTO);
        return dto;
    }

    public Map<String, SpineReference>createMapLinkSpine(Book book) {
        Map<String, SpineReference> mapLink = new HashMap<>();
        for (SpineReference ref: book.getSpine().getSpineReferences()) {
            mapLink.putIfAbsent(ref.getResource().getHref(), ref);
        }
        return mapLink;
    }

    public SpineReference getSpineByIdx(List<SpineReference> spines, int spineIdx) {
        if (spineIdx > 0 && spineIdx < spines.size()) {
            return spines.get(spineIdx);
        }
        return null;
    }

    public SpineReference getSpineFromToc(BookWrapper bookWrapper, TocItemDTO tocItemDTO) throws IOException {
        SpineReference ref = bookWrapper.getMapSpineLink().get(tocItemDTO.getHtmlHref());
        return ref;
    }

    public List<SpineReference> getSpineReferences(nl.siegmann.epublib.domain.Book book){
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

    public BookWrapper CreateBookWrapperFromBook(nl.siegmann.epublib.domain.Book book) {
        BookWrapper bookWrapper = new BookWrapper();
        BookDTO ourBook = parseEpub(book);
        bookWrapper.setBook(ourBook);
        bookWrapper.setSpines(getSpineReferences(book));
        bookWrapper.setMapSpineLink(createMapLinkSpine(book));
        return bookWrapper;
    }

    public static void main(String[] args) throws IOException {
        BookImport bookImport = new BookImport();
        Book book = bookImport.readEpubFile("dogman.epub");
//        System.out.println(book);
//        for (TocItemDTO dto: bookImport.GetTableOfContents(book)) {
//            System.out.println(dto.getTitle());
//            System.out.println(dto.getChildren());
//        }
        BookWrapper wrapper = bookImport.CreateBookWrapperFromBook(book);
        System.out.println(wrapper.getBook().getTitle());
    }
}