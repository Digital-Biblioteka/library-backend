package nsu.library.service.bookmarks;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nsu.library.dto.book.BookmarkEvent;
import nsu.library.entity.Bookmark;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;

@Component
@Slf4j
@RequiredArgsConstructor
public class BookmarkRealTimeService {
    private final SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        log.info("Received a new web socket connection");
    }

    public void handleBookmarkCreated(Bookmark bookmark) {
        BookmarkEvent event = toEvent(bookmark, BookmarkEvent.EventType.CREATED);
        messagingTemplate.convertAndSend("/topic/bookmarks/" + bookmark.getGroup().getId(), event);
    }

    public void handleBookmarkUpdated(Bookmark bookmark) {
        BookmarkEvent event = toEvent(bookmark, BookmarkEvent.EventType.UPDATED);
        messagingTemplate.convertAndSend("/topic/bookmarks/" + bookmark.getGroup().getId(), event);
    }

    public void handleBookmarkDeleted(Bookmark bookmark) {
        BookmarkEvent event = toEvent(bookmark, BookmarkEvent.EventType.DELETED);
        messagingTemplate.convertAndSend("/topic/bookmarks/" + bookmark.getGroup().getId(), event);
    }

    BookmarkEvent toEvent(Bookmark bookmark, BookmarkEvent.EventType type) {
        return new BookmarkEvent(
                type, bookmark.getId(),
                bookmark.getUser().getId(), bookmark.getGroup().getId(), bookmark.getSpine_reference(),
                bookmark.getParagraph_index(), bookmark.getText_bookmark()
        );
    }
}
