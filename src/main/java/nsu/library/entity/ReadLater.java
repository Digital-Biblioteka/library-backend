package nsu.library.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import nsu.library.util.ReadLaterId;

@Entity
@IdClass(ReadLaterId.class)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="read_later")
public class ReadLater {
    @Id
    Long userId;

    @Id
    Long bookId;
}
