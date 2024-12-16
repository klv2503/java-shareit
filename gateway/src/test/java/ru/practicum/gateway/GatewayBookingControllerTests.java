package ru.practicum.gateway;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.GateBookingController;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Проверка работы валидации для GateBookingController
@WebMvcTest(GateBookingController.class)
@ContextConfiguration(classes = ShareItGateway.class)
@RequiredArgsConstructor
public class GatewayBookingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingClient client;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final LocalDateTime startDate =
            LocalDateTime.of(2025, 3, 1, 15, 0, 0);

    private final LocalDateTime endDate =
            LocalDateTime.of(2025, 3, 10, 15, 0, 0);

    @SneakyThrows
    @Test
    public void bookItem_whenValidBodyAndHeader_thenBook() {
        long header = 1L;
        BookItemRequestDto current = BookItemRequestDto.builder()
                .itemId(1L)
                .start(startDate)
                .end(endDate)
                .build();
        when(client.bookItem(anyLong(), any(BookItemRequestDto.class)))
                .thenReturn(ResponseEntity.ok().body(current));

        RequestBuilder request = MockMvcRequestBuilders
                .post("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(current))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).bookItem(header, current);
    }

    @Test
    public void shouldNotCreateWithInvalidHeader() throws Exception {
        Long header = -1L;
        BookItemRequestDto current = BookItemRequestDto.builder()
                .itemId(1L)
                .start(startDate)
                .end(endDate)
                .build();

        RequestBuilder request = MockMvcRequestBuilders
                .post("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(current))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithoutHeader() throws Exception {
        BookItemRequestDto current = BookItemRequestDto.builder()
                .itemId(1L)
                .start(startDate)
                .end(endDate)
                .build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(current))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MissingRequestHeaderException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithoutItemId() throws Exception {
        Long header = 1L;
        BookItemRequestDto current = BookItemRequestDto.builder()
                .start(startDate)
                .end(endDate)
                .build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(current))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithoutStartDate() throws Exception {
        Long header = 1L;
        BookItemRequestDto current = BookItemRequestDto.builder()
                .itemId(-1L)
                .end(endDate)
                .build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(current))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithStartDateInPast() throws Exception {
        Long header = 1L;
        BookItemRequestDto current = BookItemRequestDto.builder()
                .itemId(-1L)
                .start(startDate.minusYears(1L))
                .end(endDate)
                .build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(current))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithoutEndDate() throws Exception {
        Long header = 1L;
        BookItemRequestDto current = BookItemRequestDto.builder()
                .itemId(-1L)
                .start(startDate)
                .build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(current))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWhenEndBeforeStart() throws Exception {
        Long header = 1L;
        BookItemRequestDto current = BookItemRequestDto.builder()
                .itemId(-1L)
                .start(startDate)
                .end(endDate.minusMonths(1L))
                .build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(current))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWhenStartEqualsEnd() throws Exception {
        Long header = 1L;
        BookItemRequestDto current = BookItemRequestDto.builder()
                .itemId(-1L)
                .start(startDate)
                .end(startDate)
                .build();
        RequestBuilder request = MockMvcRequestBuilders
                .post("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(current))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void getBooking_whenValidPathVariableAndHeader_thenGet() {
        long header = 1L;
        long bookingId = 1L;
        BookItemRequestDto current = BookItemRequestDto.builder()
                .itemId(1L)
                .start(startDate)
                .end(endDate)
                .build();
        when(client.getBooking(anyLong(), anyLong()))
                .thenReturn(ResponseEntity.ok().body(current));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings/{bookingId}", bookingId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getBooking(header, bookingId);
    }

    @Test
    public void shouldNotGetWithoutHeader() throws Exception {
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings/{bookingId}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MissingRequestHeaderException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotGetWithInvalidHeader() throws Exception {
        Long header = -1L;
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings/{bookingId}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotGetWithInvalidPathVariable() throws Exception {
        Long header = 1L;
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings/{bookingId}", -1)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void approveBooking_whenValidPathVariableAndHeaderAndParam_thenApprove() {
        long header = 1L;
        long bookingId = 1L;
        boolean approve = false;
        BookItemRequestDto current = BookItemRequestDto.builder()
                .itemId(1L)
                .start(startDate)
                .end(endDate)
                .build();
        when(client.patchBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(ResponseEntity.ok().body(current));

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/bookings/{bookingId}", bookingId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("approved", String.valueOf(approve))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).patchBooking(header, bookingId, approve);
    }

    @Test
    public void shouldNotApproveWithInvalidPathVariable() throws Exception {
        Long header = 1L;
        RequestBuilder request = MockMvcRequestBuilders
                .patch("/bookings/{bookingId}", -1)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("approved", "true")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotApproveWithoutParamApprove() throws Exception {
        Long header = 1L;
        RequestBuilder request = MockMvcRequestBuilders
                .patch("/bookings/{bookingId}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MissingServletRequestParameterException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotApproveWithInvalidParamApprove() throws Exception {
        Long header = 1L;
        RequestBuilder request = MockMvcRequestBuilders
                .patch("/bookings/{bookingId}", 1)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("approved", "tre")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentTypeMismatchException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void getOwnersBookings_whenValidPathVariableAndHeaderAndParam_thenGetList() {
        long header = 1L;
        String state = "Current";
        when(client.getOwnersBookings(anyLong(), anyString()))
                .thenReturn(ResponseEntity.ok().body(List.of()));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings/owner")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("state", state)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getOwnersBookings(header, state);
    }

    @Test
    public void shouldNotGetWithInvalidEnum() throws Exception {
        Long header = 1L;
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings/owner")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("state", "WAITNG")
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotGetListWithInvalidEnum() throws Exception {
        Long header = 1L;
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("state", "REECTED")
                .param("from", String.valueOf(0))
                .param("size", String.valueOf(10))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void getBookings_whenAllValid_thenGetList() {
        long header = 1L;
        String state = "Current";
        int from = 5;
        int size = 5;
        when(client.getBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok().body(List.of()));

        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("state", state)
                .param("from", String.valueOf(from))
                .param("size", String.valueOf(size))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getBookings(header, state, from, size);
    }

    @Test
    public void shouldNotGetListWithInvalidFrom() throws Exception {
        Long header = 1L;
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("state", "REJECTED")
                .param("from", String.valueOf(-5))
                .param("size", String.valueOf(10))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotGetListWithInvalidSize() throws Exception {
        Long header = 1L;
        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("state", "REJECTED")
                .param("from", String.valueOf(0))
                .param("size", String.valueOf(-1))
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

}
