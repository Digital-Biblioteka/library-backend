package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "groups")
@RequiredArgsConstructor
@Getter
@Setter
public class Group {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    UUID id;

    @ManyToOne
    @JoinColumn(name = "librarian_id", referencedColumnName = "id")
    User librarian;

    @Column
    String name;

    @Column
    String description;
}
