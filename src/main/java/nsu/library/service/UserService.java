package nsu.library.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import nsu.library.dto.CreateUserDTO;
import nsu.library.dto.UserDTO;
import nsu.library.entity.User;
import nsu.library.exception.UserExistsException;
import nsu.library.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;

    public User createUser(CreateUserDTO createUserDTO) {
        if (userRepository.getUsersByUsername(createUserDTO.getName()) != null) {
            throw new UserExistsException("Пользователь с таким именем уже существует");
        }
        User user = new User();
        user.setUsername(createUserDTO.getName());
        user.setPassword(createUserDTO.getPassword());
        user.setRole(createUserDTO.getRole());
        userRepository.save(user);
        return user;
    }

    public void editUser(Long id, UserDTO editUserDTO) {
        if (editUserDTO.getUserName() != null) {
            User user = userRepository.findById(id).orElseThrow( () -> new EntityNotFoundException("Пользователя с " + id + "не существует"));
            user.setUsername(editUserDTO.getUserName());
            userRepository.save(user);
        }
        if (editUserDTO.getRole() != null) {
            User user = userRepository.findById(id).orElseThrow( () -> new EntityNotFoundException("Пользователя с " + id + "не существует"));
            user.setRole(editUserDTO.getRole());
            userRepository.save(user);
        }
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow( () -> new EntityNotFoundException("Пользователя с " + id + "не существует"));
    }

    public UserDTO convertDTO(User user) {
        return new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }
}
