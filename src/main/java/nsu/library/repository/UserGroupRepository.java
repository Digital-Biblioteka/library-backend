package nsu.library.repository;

import nsu.library.entity.Group;
import nsu.library.entity.User;
import nsu.library.entity.UserGroup;
import nsu.library.util.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {
    List<UserGroup> findUserGroupByGroup_Librarian_Id(Long id);
    List<UserGroup> findByUser(User user);
    List<UserGroup> findByGroup(Group group);
}
