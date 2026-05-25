package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Table(name = "bookmark_groups")
@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class BookmarkGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="book_id", referencedColumnName = "id")
    Book book;

    @Column(nullable = false, unique = true)
    UUID accessToken;

    @Column()
    String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id")
    User owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    BookmarkVisibility visibility;

    @Column()
    Instant created_at;

    public enum BookmarkVisibility{
        PRIVATE,
        BY_LINK,
    }
}
