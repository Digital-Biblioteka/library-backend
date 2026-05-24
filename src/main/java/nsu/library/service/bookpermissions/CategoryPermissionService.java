package nsu.library.service.bookpermissions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.*;
import nsu.library.repository.*;
import nsu.library.service.groups.GroupService;
import nsu.library.service.user.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryPermissionService {

    private final CategoryPermissionRepository categoryPermissionRepository;
    private final CategoryAccessRequestRepository accessRequestRepository;
    private final CategoryLimitRequestRepository limitRequestRepository;
    private final CategoryLimitRepository categoryLimitRepository;
    private final BookCategoryRepository bookCategoryRepository;
    private final GroupService groupService;
    private final UserService userService;

    // =========================================================
    // CategoryAccessRequest  (user → librarian)
    // =========================================================

    /**
     * Пользователь создаёт запрос на доступ к категории в рамках группы.
     */
    public CategoryAccessRequest createAccessRequest(Long userId, UUID groupId, UUID categoryId) {
        groupService.getUserGroupByUserAndGroup(userId, groupId); // validates membership

        BookCategory category = bookCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        boolean alreadyPending = accessRequestRepository
                .existsByUser_IdAndGroup_IdAndCategory_IdAndStatus(
                        userId, groupId, categoryId, CategoryAccessRequest.RequestStatus.PENDING);
        if (alreadyPending) {
            throw new IllegalStateException("A pending request for this category already exists");
        }

        User user = userService.getUserById(userId);
        Group group = groupService.getGroupById(groupId);

        CategoryAccessRequest req = new CategoryAccessRequest();
        req.setUser(user);
        req.setGroup(group);
        req.setCategory(category);
        req.setStatus(CategoryAccessRequest.RequestStatus.PENDING);
        return accessRequestRepository.save(req);
    }

    public List<CategoryAccessRequest> getAccessRequestsByGroup(UUID groupId) {
        return accessRequestRepository.findByGroup_Id(groupId);
    }

    public List<CategoryAccessRequest> getAccessRequestsByUser(Long userId) {
        return accessRequestRepository.findByUser_Id(userId);
    }

    public CategoryAccessRequest getAccessRequestById(UUID id) {
        return accessRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category access request not found"));
    }

    /**
     * Библиотекарь апрувит запрос пользователя.
     * Декрементирует CategoryLimit группы и создаёт CategoryPermission на 30 дней.
     */
    @Transactional
    public CategoryAccessRequest approveAccessRequest(UUID requestId) {
        CategoryAccessRequest req = getAccessRequestById(requestId);
        if (req.getStatus() != CategoryAccessRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request is already " + req.getStatus());
        }

        CategoryLimit limit = categoryLimitRepository
                .findByGroup_IdAndCategory_Id(req.getGroup().getId(), req.getCategory().getId())
                .orElseThrow(() -> new IllegalStateException(
                        "No category limit configured for this group — request more limits from admin first"));

        if (!limit.decrementLimit()) {
            throw new IllegalStateException("Category limit exhausted for this group");
        }
        categoryLimitRepository.save(limit);

        CategoryPermission permission = new CategoryPermission();
        permission.setUser(req.getUser());
        permission.setGroup(req.getGroup());
        permission.setCategory(req.getCategory());
        permission.setTimeExpires(Instant.now().plus(30, ChronoUnit.DAYS));
        categoryPermissionRepository.save(permission);

        req.setStatus(CategoryAccessRequest.RequestStatus.APPROVED);
        return accessRequestRepository.save(req);
    }

    /**
     * Библиотекарь отклоняет запрос пользователя.
     */
    @Transactional
    public CategoryAccessRequest rejectAccessRequest(UUID requestId) {
        CategoryAccessRequest req = getAccessRequestById(requestId);
        if (req.getStatus() != CategoryAccessRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request is already " + req.getStatus());
        }
        req.setStatus(CategoryAccessRequest.RequestStatus.REJECTED);
        return accessRequestRepository.save(req);
    }

    // =========================================================
    // CategoryLimitRequest  (librarian → admin)
    // =========================================================

    /**
     * Библиотекарь просит у админа N слотов на категорию для своей группы.
     */
    public CategoryLimitRequest createLimitRequest(Long librarianId, UUID groupId,
                                                    UUID categoryId, long requestedLimit) {
        Group group = groupService.getGroupById(groupId);
        if (!group.getLibrarian().getId().equals(librarianId)) {
            throw new AccessDeniedException("You are not the librarian of this group");
        }

        BookCategory category = bookCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));

        CategoryLimitRequest req = new CategoryLimitRequest();
        req.setGroup(group);
        req.setCategory(category);
        req.setRequestedLimit(requestedLimit);
        req.setStatus(CategoryLimitRequest.RequestStatus.PENDING);
        return limitRequestRepository.save(req);
    }

    public List<CategoryLimitRequest> getLimitRequestsByGroup(UUID groupId) {
        return limitRequestRepository.findByGroup_Id(groupId);
    }

    public List<CategoryLimitRequest> getLimitRequestsByLibrarian(Long librarianId) {
        return limitRequestRepository.findByGroup_Librarian_Id(librarianId);
    }

    public List<CategoryLimitRequest> getAllLimitRequests() {
        return limitRequestRepository.findAll();
    }

    public CategoryLimitRequest getLimitRequestById(UUID id) {
        return limitRequestRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category limit request not found"));
    }

    /**
     * Админ апрувит запрос: создаёт или увеличивает CategoryLimit.
     */
    @Transactional
    public CategoryLimitRequest approveLimitRequest(UUID requestId) {
        CategoryLimitRequest req = getLimitRequestById(requestId);
        if (req.getStatus() != CategoryLimitRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request is already " + req.getStatus());
        }

        CategoryLimit limit = categoryLimitRepository
                .findByGroup_IdAndCategory_Id(req.getGroup().getId(), req.getCategory().getId())
                .orElse(null);

        if (limit == null) {
            limit = new CategoryLimit();
            limit.setGroup(req.getGroup());
            limit.setCategory(req.getCategory());
            limit.setLimit(req.getRequestedLimit());
        } else {
            limit.setLimit(limit.getLimit() + req.getRequestedLimit());
        }
        categoryLimitRepository.save(limit);

        req.setStatus(CategoryLimitRequest.RequestStatus.APPROVED);
        return limitRequestRepository.save(req);
    }

    /**
     * Админ отклоняет запрос.
     */
    @Transactional
    public CategoryLimitRequest rejectLimitRequest(UUID requestId) {
        CategoryLimitRequest req = getLimitRequestById(requestId);
        if (req.getStatus() != CategoryLimitRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("Request is already " + req.getStatus());
        }
        req.setStatus(CategoryLimitRequest.RequestStatus.REJECTED);
        return limitRequestRepository.save(req);
    }

    // =========================================================
    // Permission checks
    // =========================================================

    /**
     * Проверить, есть ли у пользователя активное разрешение на категорию, в которую входит книга.
     */
    public boolean hasActiveCategoryPermissionForBook(Long userId, Long bookId) {
        return !categoryPermissionRepository
                .findActiveByUserAndBook(userId, bookId, Instant.now())
                .isEmpty();
    }

    public List<CategoryPermission> getUserPermissions(Long userId) {
        return categoryPermissionRepository.findByUser_IdAndTimeExpiresAfter(userId, Instant.now());
    }
}
