package nsu.library.util;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Embeddable
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class UserGroupId implements Serializable {
    @Column(name="user_id")
    Long userId;
    @Column(name="group_id")
    UUID groupId;
}
