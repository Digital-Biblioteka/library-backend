package nsu.library.repository;

import nsu.library.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByUser_Id(Long userId);
    List<Review> findByBook_Id(Long bookId);

    @Query("SELECT r.book.id, AVG(r.rating) FROM Review r GROUP BY r.book.id")
    List<Object[]> findAverageRatingPerBook();

    @Query("SELECT r.book.id FROM Review r WHERE LOWER(r.review_text) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Long> findBookIdsByReviewTextContaining(@Param("query") String query);

    @Query("SELECT r.book.id, r.review_text FROM Review r WHERE LOWER(r.review_text) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Object[]> findBookReviewSnippetsByText(@Param("query") String query);
}
