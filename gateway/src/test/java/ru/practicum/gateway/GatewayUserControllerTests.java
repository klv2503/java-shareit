package ru.practicum.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.user.GateUserController;
import ru.practicum.shareit.user.UserClient;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Проверка работы валидации для GateUserController
@WebMvcTest(GateUserController.class)
@ContextConfiguration(classes = ShareItGateway.class)
@RequiredArgsConstructor
public class GatewayUserControllerTests {

    private final UserDto goodUser = new UserDto(null, "First user", "first@nowhere.net");

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserClient client;

    private final ObjectMapper objectMapper = new ObjectMapper();

    //Вспомогательный метод для реквестов create
    @SneakyThrows
    public RequestBuilder getRequest(UserDto requestBody) {
        return MockMvcRequestBuilders
                .post("/users")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
    }

    //Вспомогательный метод для реквестов update
    @SneakyThrows
    public RequestBuilder getRequest(UserDto requestBody, String path) {
        return MockMvcRequestBuilders
                .patch(path)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);
    }

    @SneakyThrows
    @Test
    public void createUser_whenValidUser_thenCreation() {
        when(client.createUser(goodUser)).thenReturn(ResponseEntity.ok().body(goodUser));
        RequestBuilder request = getRequest(goodUser);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).createUser(goodUser);
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
        //Проверяем валидацию при некорректном email
        UserDto requestBody =
                new UserDto(null, goodUser.getName(), "abrakadabra@and@nothing.net");
        RequestBuilder request = getRequest(requestBody);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void updateUser_whenValidUser_thenUpdating() {
        when(client.updateUser(1L, goodUser)).thenReturn(ResponseEntity.ok().body(goodUser));

        RequestBuilder request = getRequest(goodUser, "/users/1");
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).updateUser(1, goodUser);
    }

    @Test
    public void shouldNotUpdateWithEmptyBody() throws Exception {
        //Готовим пустые данные для апдейта и проверяем
        UserDto requestBody =
                new UserDto(null, "", "");
        Long userId = 1L;

        RequestBuilder request = getRequest(requestBody, "/users/" + userId);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateWithNegativePathVariable() throws Exception {
        //Отправляем запрос с null-header
        RequestBuilder request = getRequest(goodUser, "/users/-1");

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateWithInvalidEmail() throws Exception {
        //Готовим некорректные данные для апдейта и проверяем
        UserDto requestBody =
                new UserDto(null, "Superuser", "super@super@super.com");
        Long userId = 1L;

        RequestBuilder request = getRequest(requestBody, "/users/" + userId);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void deleteUser_whenValidId_thenErasing() {
        when(client.deleteUser(anyLong())).thenReturn(ResponseEntity.ok().body(goodUser));

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).deleteUser(1);
    }

    @Test
    public void shouldNotDeleteWithNegativePathVariable() throws Exception {
        //Отправляем запрос с null-header
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/users/-1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void getUserById_whenValidId_thenGetting() {
        when(client.getUserById(anyLong())).thenReturn(ResponseEntity.ok().body(goodUser));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getUserById(1);
    }

    @Test
    public void shouldNotGetWithNegativePathVariable() throws Exception {
        //Отправляем запрос с null-header
        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/-1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void getAllUsers_whenRequest_alwaysGettingList() {
        when(client.getAllUsers()).thenReturn(ResponseEntity.ok().body(List.of()));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getAllUsers();
    }

}
