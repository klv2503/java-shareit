package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor
public class UserControllerTests {

    private final UserDto goodUser = new UserDto(null, "First user", "first@nowhere.net");

    private final UserDto fullUser = new UserDto(1L, "First user", "first@nowhere.net");


    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService service;

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
        when(service.createUser(goodUser)).thenReturn(goodUser);
        RequestBuilder request = getRequest(goodUser);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).createUser(goodUser);
    }

    @SneakyThrows
    @Test
    public void updateUser_whenValidUser_thenUpdating() {
        when(service.updateUser(any(UserDto.class))).thenReturn(fullUser);

        RequestBuilder request = getRequest(fullUser, "/users/1");
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).updateUser(fullUser);
    }

    @SneakyThrows
    @Test
    public void deleteUser_whenValidId_thenErasing() {
        when(service.deleteUser(anyLong())).thenReturn(fullUser);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).deleteUser(1L);
    }

    @SneakyThrows
    @Test
    public void getUserById_whenValidId_thenGetting() {
        when(service.getUserById(anyLong())).thenReturn(fullUser);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users/1")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).getUserById(1L);
    }

    @SneakyThrows
    @Test
    public void getAllUsers_whenRequest_alwaysGettingList() {
        when(service.getAllUsers()).thenReturn(List.of());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/users")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).getAllUsers();
    }

}
