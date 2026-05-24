package nsu.library.repository;

import nsu.library.entity.CategoryPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface CategoryPermissionRepository extends JpaRepository<CategoryPermission, Long> {
    List<CategoryPermission> findByUser_Id(Long userId);

    /** Все активные категориальные разрешения пользователя */
    List<CategoryPermission> findByUser_IdAndTimeExpiresAfter(Long userId, Instant now);

    /** Проверка: есть ли у пользователя активное разрешение на категорию, содержащую данную книгу */
    @Query("""
            SELECT cp FROM CategoryPermission cp
            JOIN BookCategoryAssignment bca ON bca.category.id = cp.category.id
            WHERE cp.user.id = :userId
              AND bca.book.id = :bookId
              AND cp.timeExpires > :now
            """)
    List<CategoryPermission> findActiveByUserAndBook(
            @Param("userId") Long userId,
            @Param("bookId") Long bookId,
            @Param("now") Instant now);

    List<CategoryPermission> findByTimeExpiresBefore(Instant now);
}
