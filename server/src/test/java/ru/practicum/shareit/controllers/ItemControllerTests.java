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
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.dto.comments.CommentDto;
import ru.practicum.shareit.item.dto.comments.CommentInputDto;
import ru.practicum.shareit.item.dto.items.ItemDto;
import ru.practicum.shareit.item.dto.items.ItemOutputDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
@ContextConfiguration(classes = ShareItServer.class)
@RequiredArgsConstructor
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService service;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ItemDto standardItem = new ItemDto(null, "First item",
            "123456789012345678901234567890", true, 1L, null);

    private final ItemDto fullItem = new ItemDto(1L, "First item",
            "123456789012345678901234567890", true, null, null);


    private final ItemOutputDto outputItem = ItemOutputDto.builder()
            .id(1L)
            .name("noname")
            .description("without")
            .available(true)
            .owner(new User())
            .lastBooking(null)
            .nextBooking(null)
            .comments(List.of())
            .request(null)
            .build();

    private final String standardComment = "1234567890";

    @SneakyThrows
    @Test
    public void createItem_whenValidItemAndHeader_thenCreation() {
        long header = 1L;
        when(service.createItem(any(ItemDto.class)))
                .thenReturn(outputItem);

        RequestBuilder request = MockMvcRequestBuilders
                .post("/items")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(standardItem))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).createItem(standardItem);
    }

    @SneakyThrows
    @Test
    public void updateItem_whenValidItemAndPathVariableAndHeader_thenUpdate() {
        long header = 1L;
        long itemId = 1L;
        when(service.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(outputItem);

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/items/{itemId}", itemId)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(standardItem))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).updateItem(header, itemId, standardItem);
    }

    @SneakyThrows
    @Test
    public void deleteItem_whenValidPathVariableAndHeader_thenDelete() {
        long header = 1L;
        long itemId = 1L;
        when(service.deleteItem(anyLong(), anyLong())).thenReturn(outputItem);

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/items/{itemId}", itemId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).deleteItem(header, itemId);
    }

    @SneakyThrows
    @Test
    public void getItemById_whenValidPathVariable_thenGet() {
        long itemId = 1L;
        when(service.getItemById(anyLong())).thenReturn(outputItem);

        RequestBuilder request = MockMvcRequestBuilders
                .get("/items/{itemId}", itemId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).getItemById(itemId);
    }

    @SneakyThrows
    @Test
    public void getAllItemsOfOwner_whenValidHeader_thenGet() {
        long header = 1L;
        when(service.getAllItemsOfOwner(anyLong())).thenReturn(List.of());
        RequestBuilder request = MockMvcRequestBuilders
                .get("/items")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).getAllItemsOfOwner(header);
    }

    @SneakyThrows
    @Test
    public void getItemsByContext_AlwaysGet() {
        String text = "nothing";
        when(service.getItemsByContext(anyString())).thenReturn(List.of());
        RequestBuilder request = MockMvcRequestBuilders
                .get("/items/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("text", text)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).getItemsByContext(text);
    }

    @SneakyThrows
    @Test
    public void addNewComment_whenValidRequest_thenAdd() {
        long header = 1L;
        long itemId = 1L;
        CommentInputDto requestBody = new CommentInputDto(standardComment, header, itemId);
        when(service.addNewComment(any(CommentInputDto.class)))
                .thenReturn(new CommentDto());

        RequestBuilder request = MockMvcRequestBuilders
                .post("/items/{itemId}/comment", itemId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(service, times(1)).addNewComment(requestBody);
    }

}
