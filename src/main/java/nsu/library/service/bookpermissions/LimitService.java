package nsu.library.service.bookpermissions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import nsu.library.entity.BookLimit;
import nsu.library.entity.Group;
import nsu.library.repository.BookLimitRepository;
import nsu.library.repository.GroupRepository;
import nsu.library.service.books.BookService;
import nsu.library.service.groups.GroupService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LimitService {
    private final BookLimitRepository bookLimitRepository;
    private final GroupRepository groupRepository;
    private final BookService bookService;
    private final GroupService groupService;

    public BookLimit AddBookLimit(String groupID, Long bookID, Long limit) {
        BookLimit bookLimit = new BookLimit();
        Book book = bookService.getBook(bookID);
        Group group = groupService.getGroupById(groupID);
        bookLimit.setBook(book);
        bookLimit.setGroup(group);
        bookLimit.setLimit(limit);
        return bookLimitRepository.save(bookLimit);
    }

    public BookLimit EditBookLimit(Long bookLimitID, Long Limit) {
        BookLimit bookLimit = bookLimitRepository.findById(bookLimitID).orElseThrow(()-> new EntityNotFoundException("Book limit with id" + bookLimitID +  "found"));
        bookLimit.setLimit(Limit);
        return bookLimitRepository.save(bookLimit);
    }

    public BookLimit GetBookLimit(String groupID, Long bookID) {
        return bookLimitRepository.findByBook_IdAndGroup_Id(bookID, groupID);
    }

    public List<BookLimit> GetBookLimitsForGroup(String groupID) {
        Group group = groupService.getGroupById(groupID);
        return bookLimitRepository.findBookLimitsByGroup(group);
    }
}
