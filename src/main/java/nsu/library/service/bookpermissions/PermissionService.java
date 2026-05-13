package nsu.library.service.bookpermissions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.*;
import nsu.library.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    @Transactional
    public BookPermission GiveBookPermission(Long bookID, Long userID, String groupID) {
        BookPermission bookPermission = new BookPermission();
        bookPermission.setBook(bookRepository.getReferenceById(bookID));
        bookPermission.setUser(userRepository.getReferenceById(userID));
        bookPermission.setGroup(groupRepository.getReferenceById(groupID));
        bookPermission.setTimeExpires(Instant.now().plus(30, ChronoUnit.DAYS));

        BookLimit limit = bookLimitsRepository.findByBook_IdAndGroup_Id(bookID, groupID);
        boolean ok = limit.decrementLimit();
        if (!ok) {
            throw new IllegalStateException("Book limit reached for book: " + bookPermission.getBook() + " group: " + bookPermission.getGroup());
        }
        bookLimitsRepository.save(limit);

        return bookPermissionRepository.save(bookPermission);
    }

    public void RemoveBookPermission(Long bookId, Long userId) {
        bookPermissionRepository.deleteBookPermissionByBookIdAndUserId(bookId, userId);
    }

    public List<BookPermission> GetExpiredBookPermissions() {
        return bookPermissionRepository.getBookPermissionByTimeExpiresBefore(Instant.now());
    }

}
