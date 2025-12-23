package nsu.library.controller;

import lombok.RequiredArgsConstructor;
import nsu.library.dto.book.BookmarkDTO;
import nsu.library.entity.Bookmark;
import nsu.library.security.CustomUserDetails;
import nsu.library.service.books.BookmarkService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController {
    private final BookmarkService bookmarkService;

    /**
     * Create new bookmark. id of book in path, auth получен via spring security.
     *
     * @param bookId id of book in path variable
     * @param auth user
     * @param dto bookmark dto: spine idx+ paragraph idx+ text
     * @return created bookmark
     */
    @PostMapping("/{bookId}")
    public Bookmark addBookmark(@PathVariable Long bookId, Authentication auth, @RequestBody BookmarkDTO dto) {
        if (auth == null) {
            throw new AccessDeniedException("User not logged in");
        }
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return bookmarkService.addBookmark(dto,bookId, user.getUser().getId());
    }

    /**
     * get bookmarks of book by user.
     *
     * @param bookId id of book in path variable
     * @param auth user auth
     * @return list of bookmarks user created on this book
     */
    @GetMapping("/{bookId}")
    public List<Bookmark> getBookmarksByUser(@PathVariable Long bookId, Authentication auth){
        if (auth == null) {
            throw new AccessDeniedException("User not logged in");
        }
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return bookmarkService.getUserBookmarksByBook(user.getUser().getId(), bookId);
    }

    /**
     * Edit bookmark by its id. text or paragraph placement
     *
     * @param id of bookmark
     * @param auth user auth
     * @param dto bookmark dto
     * @return edited bookmark
     */
    @PutMapping("/{id}")
    public Bookmark editBookmark(@PathVariable Long id, Authentication auth, BookmarkDTO dto) {
        if (auth == null) {
            throw new AccessDeniedException("User not logged in");
        }
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        return bookmarkService.editBookmark(id, user.getUser().getId(), dto);
    }

    /**
     * delete bookmark by its id.
     * checks if user owns bookmark
     *
     * @param id of bookmark
     * @param auth user auth
     */
    @DeleteMapping("/{id}")
    public void deleteBookmark(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            throw new AccessDeniedException("User not logged in");
        }
        CustomUserDetails user = (CustomUserDetails) auth.getPrincipal();
        bookmarkService.deleteBookmark(id, user.getUser().getId());
    }
}
