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
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemRequestController.class)
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor
public class ItemRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    RequestDto standardDto = RequestDto.builder()
            .description("standard description")
            .build();

    @SneakyThrows
    @Test
    public void addNewRequest_whenValidRequest_thenCreation() {
        long header = 1L;
        when(service.addNewRequest(1L, standardDto)).thenReturn(standardDto);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/requests")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(standardDto))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).addNewRequest(header, standardDto);
    }

    @SneakyThrows
    @Test
    public void getUsersRequest_whenValidPathVariable_thenGetting() {
        long header = 1L;
        when(service.getUsersRequests(anyLong())).thenReturn(List.of());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/requests")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).getUsersRequests(header);
    }

    @SneakyThrows
    @Test
    public void getAllRequests_whenValidPathVariable_thenGetting() {
        long header = 1L;
        when(service.getAllAnotherUsersRequests(anyLong())).thenReturn(List.of());
        RequestBuilder request = MockMvcRequestBuilders
                .get("/requests/all")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).getAllAnotherUsersRequests(header);
    }

    @SneakyThrows
    @Test
    public void getRequestById_whenValidHeaderAndPathVariable_thenGetting() {
        long requestId = 1L;
        long header = 1L;
        when(service.getRequestById(anyLong(), anyLong())).thenReturn(standardDto);
        RequestBuilder request = MockMvcRequestBuilders
                .get("/requests/{requestId}", requestId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).getRequestById(header, requestId);
    }

}
