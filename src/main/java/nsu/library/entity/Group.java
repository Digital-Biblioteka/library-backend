package nsu.library.entity;

import jakarta.persistence.*;
import lombok.RequiredArgsConstructor;

@Entity
@Table(name = "groups")
@RequiredArgsConstructor
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne
    @JoinColumn(name = "librarian_id", referencedColumnName = "id")
    User librarian;

    @Column
    String name;

    @Column
    String description;
}
