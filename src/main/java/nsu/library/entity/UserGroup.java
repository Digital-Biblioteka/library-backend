package nsu.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import nsu.library.util.UserGroupId;


@Entity
@Table(name = "user_group")
@RequiredArgsConstructor
@Getter
@Setter
public class UserGroup {
   @EmbeddedId
   private UserGroupId id;

   @ManyToOne(fetch = FetchType.LAZY)
   @MapsId("userId")
   @JoinColumn(name = "user_id", nullable = false)
   private User user;

   @ManyToOne(fetch = FetchType.LAZY)
   @MapsId("groupId")
   @JoinColumn(name ="group_id", nullable = false)
   private Group group;

}
