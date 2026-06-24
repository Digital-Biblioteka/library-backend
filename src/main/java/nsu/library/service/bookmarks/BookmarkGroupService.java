package nsu.library.service.bookmarks;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.Book;
import nsu.library.entity.BookmarkGroup;
import nsu.library.entity.BookmarkGroupUser;
import nsu.library.entity.User;
import nsu.library.repository.BookRepository;
import nsu.library.repository.BookmarkGroupRepository;
import nsu.library.repository.BookmarkGroupUserRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Информация о самих группах заметок - имя, владелец, токен доступа. Не хранит инфу о заметках
 */
@Service
@RequiredArgsConstructor
public class BookmarkGroupService {
    private final BookmarkGroupRepository bookmarkGroupRepository;
    private final BookmarkGroupUserRepository bookmarkGroupUserRepository;
    private final BookRepository bookRepository;


    public BookmarkGroup createBookmarkGroup(Long bookID, User owner, String name, BookmarkGroup.BookmarkVisibility visibility) {
        BookmarkGroup bookmarkGroup = new BookmarkGroup();
        Book book = bookRepository.findById(bookID).orElseThrow(EntityNotFoundException::new);
        bookmarkGroup.setCreated_at(Instant.now());
        bookmarkGroup.setOwner(owner);
        bookmarkGroup.setVisibility(visibility);
        bookmarkGroup.setBook(book);
        bookmarkGroup.setName(name);
        bookmarkGroup.setAccessToken(UUID.randomUUID());

        return bookmarkGroupRepository.save(bookmarkGroup);
    }

    public List<BookmarkGroup> getBookmarkGroupsByBookIDAndUser(Long bookID, User user) {
        List<BookmarkGroupUser> userGroup = bookmarkGroupUserRepository.findBookmarkGroupUserByUser_IdAndGroup_Book_Id(user.getId(), bookID);
        List<BookmarkGroup> group = new ArrayList<>();
        for (BookmarkGroupUser groupUser : userGroup) {
            group.add(groupUser.getGroup());
        }
        return group;
    }

    public void deleteBookmarkGroup(UUID bookmarkGroupID) {
        bookmarkGroupRepository.deleteById(bookmarkGroupID);
    }
    
    public BookmarkGroup getBookmarkGroupByID(UUID id) {
        return bookmarkGroupRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Bookmark group with ID " + id + " not found"));
    }

    public List<BookmarkGroup> getBookmarkGroupsByName(String name) {
        return bookmarkGroupRepository.findBookmarkGroupsByName(name);
    }

    public BookmarkGroupUser giveAccessToBookmarkGroup(UUID accessToken, User user) {
        BookmarkGroup bookmarkGroup = bookmarkGroupRepository.findBookmarkGroupsByAccessToken(accessToken);
        if (bookmarkGroup == null) {
            throw new EntityNotFoundException("Bookmark group with access token " + accessToken + " not found");
        }
        BookmarkGroupUser bookmarkGroupUser = new BookmarkGroupUser();
        bookmarkGroupUser.setId(new nsu.library.util.BookmarkGroupUserId(user.getId(), bookmarkGroup.getId()));
        bookmarkGroupUser.setGroup(bookmarkGroup);
        bookmarkGroupUser.setUser(user);
        return bookmarkGroupUserRepository.save(bookmarkGroupUser);
    }

    public List<BookmarkGroupUser> getUsersByBookmarkGroup(UUID id) {
        BookmarkGroup group = getBookmarkGroupByID(id);
        return bookmarkGroupUserRepository.findBookmarkGroupUsersByGroup(group);
    }

}
