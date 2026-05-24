package nsu.library.service.bookpermissions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.*;
import nsu.library.repository.*;
import nsu.library.service.books.BookService;
import nsu.library.service.groups.GroupService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class LimitService {
    private final BookLimitRepository bookLimitRepository;
    private final BookLimitRequestRepository bookLimitRequestRepository;
    private final GroupRepository groupRepository;
    private final BookService bookService;
    private final GroupService groupService;

    // --- BookLimit CRUD (existing) ---

    public BookLimit AddBookLimit(UUID groupID, Long bookID, Long limit) {
        BookLimit bookLimit = new BookLimit();
        Book book = bookService.getBook(bookID);
        Group group = groupService.getGroupById(groupID);
        bookLimit.setBook(book);
        bookLimit.setGroup(group);
        bookLimit.setLimit(limit);
        return bookLimitRepository.save(bookLimit);
    }

    public BookLimit EditBookLimit(Long bookLimitID, Long limit) {
        BookLimit bookLimit = bookLimitRepository.findById(bookLimitID)
                .orElseThrow(() -> new EntityNotFoundException("Book limit with id " + bookLimitID + " not found"));
        bookLimit.setLimit(limit);
        return bookLimitRepository.save(bookLimit);
    }

    public BookLimit GetBookLimit(UUID groupID, Long bookID) {
        return bookLimitRepository.findByBook_IdAndGroup_Id(bookID, groupID);
    }

    public List<BookLimit> GetBookLimits() {
        return bookLimitRepository.findAll();
    }

    public List<BookLimit> GetBookLimitsForGroup(UUID groupID) {
        Group group = groupService.getGroupById(groupID);
        return bookLimitRepository.findBookLimitsByGroup(group);
    }

    // --- BookLimitRequest ---

    /**
     * Библиотекарь создаёт запрос на увеличение лимита на книгу для своей группы.
     */
    public BookLimitRequest AddLimitRequest(UUID groupID, Long bookID, long requestedLimit) {
        Book book = bookService.getBook(bookID);
        Group group = groupService.getGroupById(groupID);

        BookLimitRequest request = new BookLimitRequest();
        request.setBook(book);
        request.setGroup(group);
        request.setRequestedLimit(requestedLimit);
        request.setStatus(BookLimitRequest.RequestStatus.PENDING);
        return bookLimitRequestRepository.save(request);
    }

    /**
     * Библиотекарь смотрит свои запросы по конкретной группе.
     */
    public List<BookLimitRequest> GetLimitRequestsByGroup(UUID groupID) {
        return bookLimitRequestRepository.findByGroup_Id(groupID);
    }

    /**
     * Библиотекарь смотрит все свои запросы по всем группам.
     */
    public List<BookLimitRequest> GetLimitRequestsByLibrarian(Long librarianId) {
        return bookLimitRequestRepository.findByGroup_Librarian_Id(librarianId);
    }

    /**
     * Админ смотрит все запросы от библиотекарей.
     */
    public List<BookLimitRequest> GetAllLimitRequests() {
        return bookLimitRequestRepository.findAll();
    }

    public BookLimitRequest GetLimitRequestByID(UUID id) {
        return bookLimitRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Limit request with id " + id + " not found"));
    }

    /**
     * Админ одобряет запрос: создаёт или увеличивает BookLimit на requestedLimit.
     */
    @Transactional
    public BookLimitRequest ApproveLimitRequest(UUID requestID) {
        BookLimitRequest request = GetLimitRequestByID(requestID);

        if (request.getStatus() != BookLimitRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request is already " + request.getStatus());
        }

        BookLimit existing = bookLimitRepository.findByBook_IdAndGroup_Id(
                request.getBook().getId(), request.getGroup().getId());

        if (existing == null) {
            BookLimit newLimit = new BookLimit();
            newLimit.setBook(request.getBook());
            newLimit.setGroup(request.getGroup());
            newLimit.setLimit(request.getRequestedLimit());
            bookLimitRepository.save(newLimit);
        } else {
            existing.setLimit(existing.getLimit() + request.getRequestedLimit());
            bookLimitRepository.save(existing);
        }

        request.setStatus(BookLimitRequest.RequestStatus.APPROVED);
        return bookLimitRequestRepository.save(request);
    }

    /**
     * Админ отклоняет запрос.
     */
    @Transactional
    public BookLimitRequest RejectLimitRequest(UUID requestID) {
        BookLimitRequest request = GetLimitRequestByID(requestID);

        if (request.getStatus() != BookLimitRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request is already " + request.getStatus());
        }

        request.setStatus(BookLimitRequest.RequestStatus.REJECTED);
        return bookLimitRequestRepository.save(request);
    }
}
