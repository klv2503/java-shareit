package ru.practicum.shareit;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.*;

@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")

public class UserServiceTests {

    private final EntityManager em;

    private final Environment env;

    private final UserService service;

    private final UserMapper mapper;

    @BeforeAll
    public void setTestUsers() {
        UserDto firstUserDto = new UserDto("First user", "first@email.com");
        service.createUser(firstUserDto);
        UserDto secondUserDto = new UserDto("Second user", "second@email.com");
        service.createUser(secondUserDto);
        UserDto thirdUserDto = new UserDto("Third user", "third@email.com");
        service.createUser(thirdUserDto);
    }

    @Test
    void contextTest() {
        String activeProfile = String.join(", ", env.getActiveProfiles());
        assert activeProfile.equals("test");
        String dialect = em.getEntityManagerFactory().getProperties().get("hibernate.dialect").toString();
        assert dialect.equals("org.hibernate.dialect.H2Dialect");
        assert service != null;
    }

    @Test
    void testCreateUser() {
        UserDto userDto = new UserDto("Name of User", "some@email.com");

        service.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    //В UserServiceImpl нет реально крупных методов, этот тоже не тянет, но хоть что-то
    void shouldGetAllUsers() {

        TypedQuery<User> query = em.createQuery("Select u from User u ", User.class);
        List<UserDto> users = mapper.mapUsersListToDtoList(query.getResultList());

        System.out.println(users);

        assertNotNull(users);
        assertEquals(3, users.size());
        assertEquals("Second user", users.get(1).getName());
        assertEquals("third@email.com", users.get(2).getEmail());

    }

}
