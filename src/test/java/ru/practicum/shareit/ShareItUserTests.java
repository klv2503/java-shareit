package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.exceptions.DuplicateDataException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserMemoryStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@RequiredArgsConstructor
public class ShareItUserTests {

    private final UserDto goodUser = new UserDto(null, "First user", "first@nowhere.net");


    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserController userController;

    @Autowired
    private UserMemoryStorage userStorage;

    @Autowired
    private UserMapper userMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    public void clearUsers() {
        Map<Long, User> noUsers = new HashMap<>();
        userStorage.setUsers(noUsers);
    }

    //Вспомогательный метод для реквестов create
    public RequestBuilder getRequest(UserDto requestBody) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
    }

    //Вспомогательный метод для реквестов update
    public RequestBuilder getRequest(UserDto requestBody, Long userId) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .patch("/users/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
    }

    public void addUsersForTests() {
        Map<Long, User> users = new HashMap<>();
        users.put(1L, new User(1L, "First user", "first@nowhere.net"));
        users.put(2L, new User(2L, "Second user", "second@anyway.com"));
        userStorage.setUsers(users);
    }

    //Creation tests
    @Test
    public void shouldCorrectlyCreateNewUser() throws Exception {
        // Готовим входные данные для вызова метода
        RequestBuilder request = getRequest(goodUser);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        UserDto responseBody = objectMapper.readValue(response, UserDto.class);
        assertNotNull(responseBody);
        assertEquals(1L, responseBody.getId());
        assertEquals("First user", responseBody.getName());
        assertEquals("first@nowhere.net", responseBody.getEmail());
    }

    @Test
    public void shouldNotCreateWithInvalidName() throws Exception {
        //Проверяем валидацию при пустом имени user
        UserDto requestBody =
                new UserDto(null, "", goodUser.getEmail());
        RequestBuilder request = getRequest(requestBody);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithEmptyEmail() throws Exception {
        //Проверяем валидацию при пустом email
        UserDto requestBody =
                new UserDto(null, goodUser.getName(), "");
        RequestBuilder request = getRequest(requestBody);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithInvalidEmail() throws Exception {
        //Проверяем валидацию при пустом email
        UserDto requestBody =
                new UserDto(null, goodUser.getName(), "abrakadabra@and@nothing.net");
        RequestBuilder request = getRequest(requestBody);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithDuplicatedEmail() throws Exception {
        //Проверяем валидацию при занятом email
        addUsersForTests();
        UserDto requestBody =
                new UserDto(null, "unknown", "first@nowhere.net");
        RequestBuilder request = getRequest(requestBody);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isConflict())
                .andExpect(result ->
                        assertInstanceOf(DuplicateDataException.class, result.getResolvedException()))
                .andReturn();
    }

    //Updating tests
    @Test
    public void shouldUpdateExistedUser() throws Exception {
        //Готовим корректные данные для апдейта и проверяем
        addUsersForTests();
        UserDto requestBody =
                new UserDto(null, "Superuser", "super@super.com");
        Long userId = 1L;

        RequestBuilder request = getRequest(requestBody, userId);

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        UserDto responseBody = objectMapper.readValue(response, UserDto.class);
        assertNotNull(responseBody);
        assertEquals(1L, responseBody.getId());
        assertEquals("Superuser", responseBody.getName());
        assertEquals("super@super.com", responseBody.getEmail());
    }

    @Test
    public void shouldNotUpdateWithEmptyBody() throws Exception {
        //Готовим пустые данные для апдейта и проверяем
        addUsersForTests();
        UserDto requestBody =
                new UserDto(null, "", "");
        Long userId = 1L;

        RequestBuilder request = getRequest(requestBody, userId);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateNotExistedUser() throws Exception {
        //Готовим корректные данные для апдейта и проверяем
        addUsersForTests();
        UserDto requestBody =
                new UserDto(null, "Superuser", "super@super.com");
        Long userId = 100L;

        RequestBuilder request = getRequest(requestBody, userId);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isNotFound())
                .andExpect(result ->
                        assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateWithInvalidEmail() throws Exception {
        //Готовим некорректные данные для апдейта и проверяем
        addUsersForTests();
        UserDto requestBody =
                new UserDto(null, "Superuser", "super@super@super.com");
        Long userId = 1L;

        RequestBuilder request = getRequest(requestBody, userId);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateWithAlienEmail() throws Exception {
        //Готовим некорректные данные для апдейта и проверяем
        addUsersForTests();
        UserDto requestBody =
                new UserDto(null, "Superuser", "second@anyway.com");
        Long userId = 1L;

        RequestBuilder request = getRequest(requestBody, userId);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isConflict())
                .andExpect(result ->
                        assertInstanceOf(DuplicateDataException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldCorrectlyDeleteUser() throws Exception {
        addUsersForTests();
        Long userId = 2L;

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/users/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        UserDto responseBody = objectMapper.readValue(response, UserDto.class);
        assertNotNull(responseBody);
        assertEquals(2L, responseBody.getId());
        assertEquals("Second user", responseBody.getName());
        assertEquals("second@anyway.com", responseBody.getEmail());
        assertEquals(1, userStorage.getUsers().size());
    }

    @Test
    public void shouldGiveExceptionByDeletingNonExistedUser() throws Exception {
        addUsersForTests();
        Long userId = 100L;

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/users/{userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isNotFound())
                .andExpect(result ->
                        assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andReturn();
    }

}
