package ru.practicum.shareit.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingOutputDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BookingController.class)
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor
public class BookingControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService service;

    @MockBean
    private UserService userService;

    @MockBean
    private ItemService itemService;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private final LocalDateTime startDate =
            LocalDateTime.of(2025, 3, 1, 15, 0, 0);

    private final LocalDateTime endDate =
            LocalDateTime.of(2025, 3, 10, 15, 0, 0);

    @SneakyThrows
    @Test
    public void addNewBooking_whenValidBodyAndHeader_thenBook() {
        long header = 1L;
        BookingInputDto current = BookingInputDto.builder()
                .id(header)
                .start(startDate)
                .end(endDate)
                .booker(1L)
                .status("WAITING")
                .build();
        when(service.addNewBooking(any(BookingInputDto.class)))
                .thenReturn(new BookingOutputDto());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/bookings")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(current))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).addNewBooking(current);
    }

    @SneakyThrows
    @Test
    public void getBookingInfo_whenValidPathVariableAndHeader_thenGet() {
        long header = 1L;
        long bookingId = 1L;
        BookingInputDto current = BookingInputDto.builder()
                .itemId(1L)
                .start(startDate)
                .end(endDate)
                .build();
        when(service.getBookingInfo(anyLong()))
                .thenReturn(new BookingOutputDto());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings/{bookingId}", bookingId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).getBookingInfo(bookingId);
    }

    @SneakyThrows
    @Test
    public void approveBooking_whenValidPathVariableAndHeaderAndParam_thenApprove() {
        long header = 1L;
        long bookingId = 1L;
        boolean approve = false;
        BookingInputDto current = BookingInputDto.builder()
                .itemId(1L)
                .start(startDate)
                .end(endDate)
                .build();
        when(service.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(new BookingOutputDto());

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/bookings/{bookingId}", bookingId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("approved", String.valueOf(approve))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).approveBooking(header, bookingId, approve);
    }

    @SneakyThrows
    @Test
    public void getAllUsersBookings_whenAllValid_thenGetList() {
        long header = 1L;
        String state = "Current";
        int from = 5;
        int size = 5;
        when(service.getAllUsersBookings(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of());

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
        verify(service, times(1)).getAllUsersBookings(header, state, from, size);
    }

    @SneakyThrows
    @Test
    public void getAllOwnersBookings_whenAllValid_thenGetList() {
        long header = 1L;
        String state = "Current";
        User user = new User(1L, "name", "a@email.net");
        List<Item> items = List.of();
        when(userService.getUser(anyLong()))
                .thenReturn(user);
        when(itemService.getItemsList(user))
                .thenReturn(items);
        when(service.getAllOwnersBookings(items, state))
                .thenReturn(List.of());

        RequestBuilder request = MockMvcRequestBuilders
                .get("/bookings/owner")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .param("state", state)
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(userService, times(1)).getUser(header);
        verify(itemService, times(1)).getItemsList(user);
        verify(service, never()).getAllOwnersBookings(items, state);

    }

}
