package ru.practicum.shareit.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.auxiliary.exceptions.DuplicateDataException;
import ru.practicum.shareit.auxiliary.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Sql({"/schema.sql", "/data.sql"})
@ActiveProfiles("test")

public class UserServiceTests {

    private final EntityManager em;

    private final Environment env;

    private final UserService service;

    @Test
    void contextTest() {
        String activeProfile = String.join(", ", env.getActiveProfiles());
        assert activeProfile.equals("test");
        String dialect = em.getEntityManagerFactory().getProperties().get("hibernate.dialect").toString();
        assert dialect.equals("org.hibernate.dialect.H2Dialect");
        assert service != null;
    }

    @Test
    public void testCreateUser() {
        UserDto userDto = new UserDto("Name of User", "noch_eine@email.com");
        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    public void createUser_whenUsedEmail_thenDuplicateError() {
        UserDto userDto = new UserDto("Name of User", "first@email.com");
        assertThrows(DuplicateDataException.class, () -> service.createUser(userDto));
    }

    @Test
    public void updateUser_whenCorrectData_thenUpdate() {
        UserDto userDto = new UserDto(3L,"Name of User", "noch_eine@email.com");
        service.updateUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertNotNull(user);
        assertEquals(userDto.getName(), user.getName());
    }

    @Test
    public void updateUser_whenOnlyNewEmail_thenPartialUpdate() {
        UserDto userDto = new UserDto(3L,"", "noch_eine@email.com");
        service.updateUser(userDto);

        User userAfterUpdate = service.getUser(3L);

        assertNotNull(userAfterUpdate);
        assertEquals("Third user", userAfterUpdate.getName());
        assertEquals(userDto.getEmail(), userAfterUpdate.getEmail());
    }

    @Test
    public void updateUser_whenOnlyNewName_thenPartialUpdate() {
        UserDto userDto = new UserDto(3L,"New name", "");
        service.updateUser(userDto);

        User userAfterUpdate = service.getUser(3L);

        assertNotNull(userAfterUpdate);
        assertEquals(userDto.getName(), userAfterUpdate.getName());
        assertEquals("third@email.com", userAfterUpdate.getEmail());
    }

    @Test
    public void deleteUser_whenCorrectId_thenDelete() {
        List<UserDto> users = service.getAllUsers();
        int initialSize = users.size();
        UserDto singleUser = users.getFirst();

        service.deleteUser(singleUser.getId());

        List<UserDto> usersNewList = service.getAllUsers();
        assertEquals(initialSize - 1, usersNewList.size());
        assertFalse(usersNewList.contains(singleUser));
    }

    @Test
    public void deleteUser_whenWrongId_thenNotFoundException() {
        assertThrows(NotFoundException.class, () -> service.deleteUser(100L));
    }

    @Test
    public void getUserById_shouldGetUserDtoOfExistedUser() {
        User user = service.getUser(1L);

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("First user", user.getName());
        assertEquals("first@email.com", user.getEmail());
    }

    @Test
    public void getUserById_shouldNotGetUserDto() {
        assertThrows(NotFoundException.class, () -> service.getUserById(100L));
    }

    @Test
    public void getUser_shouldGetExistedUser() {
        User user = service.getUser(1L);

        assertNotNull(user);
        assertEquals(1, user.getId());
        assertEquals("First user", user.getName());
        assertEquals("first@email.com", user.getEmail());
    }

    @Test
    public void getUser_shouldNotGetUser() {
        assertThrows(NotFoundException.class, () -> service.getUser(100L));
    }

    @Test
    public void shouldGetAllUsers() {

        List<UserDto> users = service.getAllUsers();

        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals("Second user", users.get(1).getName());
        assertEquals("third@email.com", users.get(2).getEmail());

    }

}
