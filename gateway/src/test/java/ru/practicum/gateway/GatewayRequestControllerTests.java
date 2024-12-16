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
import org.springframework.web.bind.MissingRequestHeaderException;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.request.GateItemRequestController;
import ru.practicum.shareit.request.ItemRequestClient;
import ru.practicum.shareit.request.dto.RequestDto;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Проверка работы валидации для GateItemRequestController
@WebMvcTest(GateItemRequestController.class)
@ContextConfiguration(classes = ShareItGateway.class)
@RequiredArgsConstructor
public class GatewayRequestControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemRequestClient client;

    private final ObjectMapper objectMapper = new ObjectMapper();

    RequestDto standardDto = RequestDto.builder()
            .description("standard description")
            .build();

    @SneakyThrows
    @Test
    public void addNewRequest_whenValidRequest_thenCreation() {
        long header = 1L;
        when(client.addNewRequest(1L, standardDto)).thenReturn(ResponseEntity.ok().body(standardDto));
        RequestBuilder request = MockMvcRequestBuilders
                .post("/requests")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(standardDto))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).addNewRequest(header, standardDto);
    }

    @Test
    public void shouldNotCreateWithInvalidHeader() throws Exception {
        Long header = -1L;
        RequestBuilder request = MockMvcRequestBuilders
                .post("/requests")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(standardDto))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithoutHeader() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .post("/requests")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(standardDto))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MissingRequestHeaderException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithEmptyDescription() throws Exception {
        Long header = 1L;
        RequestBuilder request = MockMvcRequestBuilders
                .post("/requests")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RequestDto.builder().description("   ").build()))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithToLongDescription() throws Exception {
        Long header = 1L;
        RequestBuilder request = MockMvcRequestBuilders
                .post("/requests")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(RequestDto.builder()
                        .description("12345".repeat(50))
                        .build()))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void getUsersRequest_whenValidPathVariable_thenGetting() {
        long header = 1L;
        when(client.getUsersRequests(anyLong())).thenReturn(ResponseEntity.ok().body(standardDto));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/requests")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getUsersRequests(header);
    }

    @Test
    public void shouldNotGetWithInvalidHeader() throws Exception {
        Long header = -1L;
        RequestBuilder request = MockMvcRequestBuilders
                .get("/requests")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotGetWithoutHeader() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/requests")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(standardDto))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MissingRequestHeaderException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void getAllRequests_whenValidPathVariable_thenGetting() {
        long header = 1L;
        when(client.getAllRequests(anyLong())).thenReturn(ResponseEntity.ok().body(standardDto));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/requests/all")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getAllRequests(header);
    }

    @SneakyThrows
    @Test
    public void getRequestById_whenValidHeaderAndPathVariable_thenGetting() {
        long requestId = 1L;
        long header = 1L;
        when(client.getRequestById(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().body(standardDto));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/requests/{requestId}", requestId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getRequestById(header, requestId);
    }

}
