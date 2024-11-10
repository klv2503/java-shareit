package ru.practicum.shareit.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Qualifier("userMemoryStorage")
@Data
@RequiredArgsConstructor
public class UserMemoryStorage {

    private Map<Long, User> users = new HashMap<>();

    public User addNewUser(User user) {
        user.setId(getNewId());
        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user; //пока возвращается новое значение
    }

    public User deleteUser(Long id) {
        return users.remove(id);
    }

    public User getUserById(Long id) {
        return users.get(id);
    }

    public List<User> getAllUsers() {
        return users.keySet().stream()
                .map(users::get)
                .toList();
    }

    public boolean isEmailExists(String email) {
        return users.keySet().stream()
                .map(users::get)
                .anyMatch(user -> user.getEmail().equals(email));
    }

    public boolean isEmailExists(Long id, String email) {
        return users.keySet().stream()
                .filter(l -> !l.equals(id))
                .map(users::get)
                .anyMatch(user -> user.getEmail().equals(email));
    }

    public Long getNewId() {
        long id = users.keySet().stream()
                .mapToLong(l -> l)
                .max().orElse(0);
        return ++id;
    }
}
