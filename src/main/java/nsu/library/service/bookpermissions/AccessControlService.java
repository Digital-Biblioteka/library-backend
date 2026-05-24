package nsu.library.service.bookpermissions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.*;
import nsu.library.repository.*;
import nsu.library.service.books.BookService;
import nsu.library.repository.CategoryPermissionRepository;
import nsu.library.service.groups.GroupService;
import nsu.library.service.user.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class AccessControlService {
    private final UserRepository userRepository;
    private final BookPermissionRepository bookPermissionRepository;
    private final BookRepository bookRepository;
    private final AccessRequestRepository accessRequestRepository;
    private final BookService bookService;
    private final GroupService groupService;
    private final UserService userService;
    private final CategoryPermissionRepository categoryPermissionRepository;

    public List<BookAccessRequest> GetAccessRequestsByGroup(UUID groupID) {
        return accessRequestRepository.getAccessRequestsByGroup_Id(groupID);
    }

    public void DeleteAccessRequestsByID(UUID id) {
        accessRequestRepository.deleteById(id);
    }

    public BookAccessRequest GetAccessRequestByID(UUID id) {
        return accessRequestRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public BookAccessRequest AddAccessRequest(Long bookID, Long userID, UUID groupID) {
        Book book = bookService.getBook(bookID);
        Group group = groupService.getGroupById(groupID);
        UserGroup ug = groupService.getUserGroupByUserAndGroup(userID, groupID);
        User user = userService.getUserById(userID);

        boolean alreadyPending = accessRequestRepository
                .getAccessRequestsByUser_Id(userID).stream()
                .anyMatch(r -> r.getBook().getId().equals(bookID)
                        && r.getGroup().getId().equals(groupID)
                        && r.getStatus() == BookAccessRequest.RequestStatus.PENDING);
        if (alreadyPending) {
            throw new IllegalStateException("A pending request for this book in this group already exists");
        }

        BookAccessRequest accessRequest = new BookAccessRequest();
        accessRequest.setBook(book);
        accessRequest.setGroup(group);
        accessRequest.setUser(user);
        accessRequest.setStatus(BookAccessRequest.RequestStatus.PENDING);
        return accessRequestRepository.save(accessRequest);
    }

    public List<BookAccessRequest> GetAccessRequestsByUserId(Long userID) {
        return accessRequestRepository.getAccessRequestsByUser_Id(userID);
    }

    public List<BookPermission> ListBookPermissionsByUserId(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new EntityNotFoundException("User with id " + userId + " not found");
        }

        return bookPermissionRepository.getBookPermissionsByUser(user);
    }

    public List<BookPermission> ListBookPermissionsByBookId(Long bookId) {
        Book book = bookRepository.findById(bookId).orElse(null);
        if (book == null) {
            throw new EntityNotFoundException("Book with id " + bookId + " not found");
        }

        return bookPermissionRepository.getBookPermissionsByBook(book);
    }

    public BookAccessRequest SaveAccessRequest(BookAccessRequest request) {
        return accessRequestRepository.save(request);
    }

    public boolean CheckPermissionToBook(Long bookID, Long userID) {
        Book book = bookRepository.findById(bookID).orElseThrow(() -> new EntityNotFoundException("Book with id " + bookID + " not found"));
        User user = userRepository.findById(userID).orElseThrow(() -> new EntityNotFoundException("User with id " + userID + " not found"));

        if (book.getPublicity() == Book.PublicityType.PUBLIC) {
            return true;
        }

        List<BookPermission> bookPermission = bookPermissionRepository.findBookPermissionsByBookAndUser(book, user);
        if (!bookPermission.isEmpty()) return true;

        return !categoryPermissionRepository
                .findActiveByUserAndBook(userID, bookID, java.time.Instant.now())
                .isEmpty();
    }
}
