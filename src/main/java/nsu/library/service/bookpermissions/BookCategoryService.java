package nsu.library.service.bookpermissions;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.category.BookCategoryDTO;
import nsu.library.entity.*;
import nsu.library.repository.*;
import nsu.library.util.BookCategoryId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookCategoryService {

    private final BookCategoryRepository bookCategoryRepository;
    private final BookCategoryAssignmentRepository assignmentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;


    public List<BookCategory> getAllCategories() {
        return bookCategoryRepository.findAll();
    }

    public BookCategory getCategoryById(UUID id) {
        return bookCategoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category with id " + id + " not found"));
    }

    public BookCategory createCategory(BookCategoryDTO dto, Long creatorId) {
        if (bookCategoryRepository.existsByName(dto.getName())) {
            throw new IllegalStateException("Category with name '" + dto.getName() + "' already exists");
        }
        User creator = userRepository.getReferenceById(creatorId);
        BookCategory category = new BookCategory();
        category.setName(dto.getName());
        category.setDescription(dto.getDescription());
        category.setCreatedBy(creator);
        return bookCategoryRepository.save(category);
    }

    public BookCategory updateCategory(UUID id, BookCategoryDTO dto) {
        BookCategory category = getCategoryById(id);
        if (dto.getName() != null) {
            category.setName(dto.getName());
        }
        if (dto.getDescription() != null) {
            category.setDescription(dto.getDescription());
        }
        return bookCategoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(UUID id) {
        if (!bookCategoryRepository.existsById(id)) {
            throw new EntityNotFoundException("Category with id " + id + " not found");
        }
        assignmentRepository.deleteAll(assignmentRepository.findByCategory_Id(id));
        bookCategoryRepository.deleteById(id);
    }

    // --- Book-Category assignments ---

    @Transactional
    public BookCategoryAssignment assignBookToCategory(Long bookId, UUID categoryId) {
        if (!bookRepository.existsById(bookId)) {
            throw new EntityNotFoundException("Book with id " + bookId + " not found");
        }
        BookCategory category = getCategoryById(categoryId);

        if (assignmentRepository.existsByBook_IdAndCategory_Id(bookId, categoryId)) {
            throw new IllegalStateException("Book is already in this category");
        }

        Book book = bookRepository.getReferenceById(bookId);
        BookCategoryAssignment assignment = new BookCategoryAssignment();
        assignment.setId(new BookCategoryId(bookId, categoryId));
        assignment.setBook(book);
        assignment.setCategory(category);
        return assignmentRepository.save(assignment);
    }

    @Transactional
    public void removeBookFromCategory(Long bookId, UUID categoryId) {
        BookCategoryId id = new BookCategoryId(bookId, categoryId);
        if (!assignmentRepository.existsById(id)) {
            throw new EntityNotFoundException("Book is not in this category");
        }
        assignmentRepository.deleteById(id);
    }

    public List<BookCategoryAssignment> getBooksInCategory(UUID categoryId) {
        getCategoryById(categoryId); // validates existence
        return assignmentRepository.findByCategory_Id(categoryId);
    }

    public List<BookCategoryAssignment> getCategoriesForBook(Long bookId) {
        return assignmentRepository.findByBook_Id(bookId);
    }
}
