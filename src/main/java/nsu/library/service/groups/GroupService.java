package nsu.library.service.groups;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nsu.library.dto.group.GroupDTO;
import nsu.library.entity.Group;
import nsu.library.entity.User;
import nsu.library.entity.UserGroup;
import nsu.library.repository.GroupRepository;
import nsu.library.repository.UserGroupRepository;
import nsu.library.repository.UserRepository;
import nsu.library.util.UserGroupId;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;

    public Group createGroup(Long librarianID, String name, String description) {
        Group group = new Group();
        User librarian = userRepository.findById(librarianID).orElseThrow(EntityNotFoundException::new);
        group.setLibrarian(librarian);
        group.setName(name);
        group.setDescription(description);
        return groupRepository.save(group);
    }

    public Group updateGroup(String groupID, GroupDTO req) {
        Group group = groupRepository.findById(groupID).orElseThrow(EntityNotFoundException::new);
        if (req.getLibrarianID() != null) {
            group.setLibrarian(userRepository.findById(req.getLibrarianID()).orElseThrow(EntityNotFoundException::new));
        }
        if (req.getName() != null) {
            group.setName(req.getName());
        }
        if (req.getDescription() != null) {
            group.setDescription(req.getDescription());
        }
        return groupRepository.save(group);
    }

    public Group getGroupById(String id) {
        return groupRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }

    public UserGroup getUserGroupByUserAndGroup(Long userID, String groupID) {
        if (!userRepository.existsById(userID)) {
            throw new EntityNotFoundException("User with ID " + userID + " not found");
        }
        if (!groupRepository.existsById(groupID)) {
            throw new EntityNotFoundException("Group with ID " + groupID + " not found");
        }

        return userGroupRepository.findById(new UserGroupId(userID, groupID)).
                orElseThrow( () -> new AccessDeniedException("User with ID " + userID +
                        "is not associated with group with ID " + groupID));
    }

    public List<UserGroup> GetUsersByLibrarian(Long librarianId) {
        return userGroupRepository.findUserGroupByGroup_Librarian_Id(librarianId);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupByName(String name) {
        return groupRepository.getGroupsByName(name);
    }

    public List<Group> getGroupsByUser(User user) {
        List<UserGroup> userGroups = userGroupRepository.findByUser(user);
        List<Group> groups = new ArrayList<>();
        for (UserGroup userGroup : userGroups) {
            groups.add(userGroup.getGroup());
        }
        return groups;
    }

    @Transactional
    public UserGroup AddUserToGroup(Long userID, String groupID) {
        if (!userRepository.existsById(userID)) {
            throw new EntityNotFoundException("User with ID " + userID + " not found");
        }
        if (!groupRepository.existsById(groupID)) {
            throw new EntityNotFoundException("Group with ID " + groupID + " not found");
        }

        User user = userRepository.getReferenceById(userID);
        Group group = groupRepository.getReferenceById(groupID);

        UserGroup newUserGroup = new UserGroup();
        newUserGroup.setId(new UserGroupId(userID, groupID));
        newUserGroup.setUser(user);
        newUserGroup.setGroup(group);

        return userGroupRepository.save(newUserGroup);
    }

    @Transactional
    public void RemoveUserFromGroup(Long userID, String groupID) {
        UserGroupId id = new UserGroupId(userID, groupID);
        if (!userGroupRepository.existsById(id)) {
            throw new EntityNotFoundException("User is not in group " + id);
        }
        userGroupRepository.deleteById(id);
    }
}
