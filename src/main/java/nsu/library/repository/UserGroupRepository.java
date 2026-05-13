package nsu.library.repository;

import nsu.library.entity.UserGroup;
import nsu.library.util.UserGroupId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserGroupRepository extends JpaRepository<UserGroup, UserGroupId> {
}
