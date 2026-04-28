package nsu.library.service.groups;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import nsu.library.entity.Group;
import nsu.library.entity.User;
import nsu.library.entity.UserGroup;
import nsu.library.repository.GroupRepository;
import nsu.library.repository.UserGroupRepository;
import nsu.library.repository.UserRepository;
import nsu.library.util.UserGroupId;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GroupService {
    private final GroupRepository groupRepository;
    private final UserGroupRepository userGroupRepository;
    private final UserRepository userRepository;

    public Group createGroup(Group group) {
        return groupRepository.save(group);
    }

    public Group getGroupById(String id) {
        return groupRepository.getReferenceById(id);
    }

    public List<Group> getAllGroups() {
        return groupRepository.findAll();
    }

    public Group getGroupByName(String name) {
        return groupRepository.getGroupsByName(name);
    }

    @Transactional
    public void AddUserToGroup(Long userID, String groupID) {
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

        userGroupRepository.save(newUserGroup);
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
