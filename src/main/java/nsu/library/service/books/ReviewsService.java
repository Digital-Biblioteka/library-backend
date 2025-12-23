package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.review.ReviewDTO;
import nsu.library.entity.Book;
import nsu.library.entity.Review;
import nsu.library.entity.User;
import nsu.library.repository.BookRepository;
import nsu.library.repository.ReviewRepository;
import nsu.library.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * crud for reviews
 */
@Service
@RequiredArgsConstructor
public class ReviewsService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    /**
     * create review tied to both user and book.
     *
     * @param dto with review rating and text(optional)
     * @param userId user
     * @param bookId book
     * @return created review
     */
    public Review createReview(ReviewDTO dto, Long userId, Long bookId) {
        Review review = new Review();
        review.setRating(dto.getRating());
        review.setReview_text(dto.getComment());
        User user = userRepository.findById(userId).orElseThrow();
        Book book = bookRepository.findById(bookId).orElseThrow();
        review.setUser(user);
        review.setBook(book);
        return reviewRepository.save(review);
    }

    public Review getReview(Long id) {
        return reviewRepository.findById(id).orElseThrow();
    }

    /**
     * filter reviews by user.
     *
     * @param userId user
     * @return list of reviews user wrote
     */
    public List<Review> getReviewsByUser(Long userId) {
        return reviewRepository.findByUser_Id(userId);
    }

    /**
     * filter reviews by book
     * @param bookId id of book
     * @return list of reviews written for this book
     */
    public List<Review> getReviewsByBook(Long bookId) {
        return reviewRepository.findByBook_Id(bookId);
    }

    public Review editReview(ReviewDTO dto, Long userId, Long reviewId) {
        Review review = getReview(reviewId);
        if (!Objects.equals(review.getUser().getId(), userId)) {
            throw new AccessDeniedException("You do not have permission to edit this review");
        }
        if (dto.getRating() != 0) {
            review.setRating(dto.getRating());
        }
        if (dto.getComment() != null) {
            review.setReview_text(dto.getComment());
        }
        return reviewRepository.save(review);
    }

    public Review deleteReview(Long userId, Long id) {
        Review review = getReview(id);
        if (!Objects.equals(review.getUser().getId(), userId)) {
            throw new AccessDeniedException("You do not have permission to delete this review");
        }
        reviewRepository.delete(review);
        return review;
    }
}
