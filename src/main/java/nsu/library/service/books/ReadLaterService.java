package nsu.library.service.books;

import lombok.RequiredArgsConstructor;
import nsu.library.entity.ReadLater;
import nsu.library.repository.ReadLaterRepository;
import nsu.library.util.ReadLaterId;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReadLaterService {
    private final ReadLaterRepository readLaterRepository;

    public ReadLater addBookToReadLater(Long userId, Long bookId) {
        ReadLater readLater = new ReadLater();
        readLater.setUserId(userId);
        readLater.setBookId(bookId);
        return readLaterRepository.save(readLater);
    }

    public void removeBookFromReadLater(Long userId, Long bookId) {
        ReadLaterId id = new ReadLaterId();
        id.setUserId(userId);
        id.setBookId(bookId);
        readLaterRepository.deleteById(id);
    }
}
