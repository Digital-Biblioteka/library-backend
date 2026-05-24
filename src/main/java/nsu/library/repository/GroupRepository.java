package nsu.library.repository;

import nsu.library.entity.Group;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface GroupRepository extends JpaRepository<Group, UUID> {
    Group getGroupsByName(String name);
    List<Group> findGroupsByLibrarian_Id(Long librarianID);
}
