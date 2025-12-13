package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.ReviewDTO;
import nsu.library.entity.Review;
import nsu.library.entity.User;
import nsu.library.service.books.ReviewsService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * crud for reviews. most methods self-explanatory
 */
@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewsService reviewsService;

    /**
     * create review using auth of spring security to extract user id.
     * @param reviewDTO dto of review
     * @param bookId book
     * @param auth auth of user
     * @return created review
     */
    @PostMapping("/books/reviews/{bookId}")
    public Review createReview(@RequestBody ReviewDTO reviewDTO, @PathVariable Long bookId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        return reviewsService.createReview(reviewDTO, user.getId(), bookId);
    }

    @PutMapping("/reviews/{reviewId}")
    public Review editReview(@RequestBody ReviewDTO reviewDTO, @PathVariable Long reviewId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        return reviewsService.editReview(reviewDTO, user.getId(), reviewId);
    }

    @GetMapping("/profile/reviews")
    public List<Review> getUserReviews(Authentication auth) {
        User user = (User) auth.getPrincipal();
        return reviewsService.getReviewsByUser(user.getId());
    }

    @GetMapping("/books/reviews/{bookId}")
    public List<Review> getBookReviews(@PathVariable Long bookId) {
        return reviewsService.getReviewsByBook(bookId);
    }

    @GetMapping("/reviews/{reviewId}")
    public Review getReview(@PathVariable Long reviewId) {
        return reviewsService.getReview(reviewId);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public Review deleteReview(@PathVariable Long reviewId, Authentication auth) {
        User user = (User) auth.getPrincipal();
        return reviewsService.deleteReview(user.getId(), reviewId);
    }
}
