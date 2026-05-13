package nsu.library.repository;

import nsu.library.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GroupRepository extends JpaRepository<Group, String> {
    Group getGroupsByName(String name);
}
