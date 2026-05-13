package nsu.library.service.bookpermissions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.permissions.AccessRequest;
import nsu.library.entity.*;
import nsu.library.repository.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final BookPermissionRepository bookPermissionRepository;
    private final BookLimitRepository bookLimitsRepository;
    private final AccessRequestRepository accessRequestRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final GroupRepository groupRepository;

    public List<BookAccessRequest> GetAccessRequestsByGroup(String groupID) {
        return accessRequestRepository.getAccessRequestsByGroup_Id(groupID);
    }

    public void DeleteAccessRequestsByID(String id) {
        accessRequestRepository.deleteById(id);
    }

    public BookAccessRequest GetAccessRequestByID(String id) {
        return accessRequestRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public BookAccessRequest AddAccessRequest(Long bookID, Long userID, String groupID) {
        BookAccessRequest accessRequest = new BookAccessRequest();

        accessRequest.setBook(bookRepository.getReferenceById(bookID));
        accessRequest.setUser(userRepository.getReferenceById(userID));
        accessRequest.setGroup(groupRepository.getReferenceById(groupID));

        return accessRequestRepository.save(accessRequest);
    }

    //TODO: при добавлении permission, нужно лимиты уменьшить
    public void GiveBookPermission(BookPermission bookPermission) {
        BookLimit limit = bookLimitsRepository.findByBookAndGroup(bookPermission.getBook(), bookPermission.getGroup());
        boolean ok = limit.decrementLimit();
        if (!ok) {
            throw new IllegalStateException("Book limit reached for book: " + bookPermission.getBook() + " group: " + bookPermission.getGroup());
        }

        bookPermissionRepository.save(bookPermission);
    }

    public void RemoveBookPermission(Long bookId, Long userId) {
        bookPermissionRepository.deleteBookPermissionByBookIdAndUserId(bookId, userId);
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


    public List<BookPermission> GetExpiredBookPermissions() {
        return bookPermissionRepository.getBookPermissionByTimeExpiresBefore(Instant.now());
    }
}
