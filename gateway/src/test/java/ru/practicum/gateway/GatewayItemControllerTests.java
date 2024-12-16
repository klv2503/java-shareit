package ru.practicum.gateway;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import ru.practicum.shareit.item.GateItemController;
import ru.practicum.shareit.item.ItemClient;
import ru.practicum.shareit.item.dto.CommentInputDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//Проверка работы валидации для GateItemController
@WebMvcTest(GateItemController.class)
@ContextConfiguration(classes = ShareItGateway.class)
@RequiredArgsConstructor
public class GatewayItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemClient client;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final ItemDto standardItem = new ItemDto(null, "First item",
            "123456789012345678901234567890", true, null, null);

    private final String standardComment = "1234567890";

    //Вспомогательный метод для запросов create
    public RequestBuilder getRequest(ItemDto requestBody, Long header) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post("/items")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
    }

    //Вспомогательный метод для запросов update
    public RequestBuilder getRequest(ItemDto requestBody, Long header, Long pathVar) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .patch("/items/{itemId}", pathVar)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
    }

    @SneakyThrows
    @Test
    public void createItem_whenValidItemAndHeader_thenCreation() {
        long header = 1L;
        ItemDto expectedItem = ItemDto.builder()
                .name(standardItem.getName())
                .description(standardItem.getDescription())
                .available(true)
                .owner(header)
                .build();
        when(client.createItem(anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().body(standardItem));

        RequestBuilder request = MockMvcRequestBuilders
                .post("/items")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(standardItem))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).createItem(header, expectedItem);
    }

    @Test
    public void shouldNotCreateWithInvalidHeader() throws Exception {
        Long header = -1L;
        RequestBuilder request = getRequest(standardItem, header);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithInvalidName() throws Exception {
        Long header = 1L;
        //Проверяем валидацию при пустом наименовании item
        ItemDto requestBody =
                new ItemDto(null, "", standardItem.getDescription(), true, null, null);
        RequestBuilder request = getRequest(requestBody, header);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithToLongName() throws Exception {
        Long header = 1L;
        //Проверяем валидацию при пустом наименовании item
        ItemDto requestBody =
                new ItemDto(null, standardItem.getName().repeat(10), standardItem.getDescription(), true, null, null);
        RequestBuilder request = getRequest(requestBody, header);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithoutDescription() throws Exception {
        //Проверяем валидацию при пустом описании item
        Long header = 1L;
        ItemDto requestBody =
                new ItemDto(null, standardItem.getName(), "", true, null, null);
        RequestBuilder request = getRequest(requestBody, header);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithToLongDescription() throws Exception {
        //Проверяем валидацию при слишком длинном описании item
        Long header = 1L;
        ItemDto requestBody =
                new ItemDto(null, standardItem.getName(), standardItem.getDescription().repeat(10), true, null, null);
        RequestBuilder request = getRequest(requestBody, header);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithInvalidAvailable() throws Exception {
        //Проверяем отсутствие available
        Long header = 1L;
        ItemDto requestBody =
                new ItemDto(null, standardItem.getName(), standardItem.getDescription(), null, null, null);
        RequestBuilder request = getRequest(requestBody, header);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void updateItem_whenValidItemAndPathVariableAndHeader_thenUpdate() {
        long header = 1L;
        long itemId = 1L;
        when(client.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenReturn(ResponseEntity.ok().body(standardItem));

        RequestBuilder request = MockMvcRequestBuilders
                .patch("/items/{itemId}", itemId)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(standardItem))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();

        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).updateItem(header, itemId, standardItem);
    }

    @Test
    public void shouldNotUpdateWithInvalidHeader() throws Exception {
        Long header = -1L;
        Long pathVar = 1L;

        RequestBuilder request = getRequest(standardItem, header, pathVar);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateWithEmptyBody() throws Exception {
        //Готовим пустые данные для апдейта и проверяем
        ItemDto requestBody =
                new ItemDto(null, "", "", null, null, null);
        Long header = 1L;
        Long pathVar = 1L;

        RequestBuilder request = getRequest(requestBody, header, pathVar);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateWithToLongNewName() throws Exception {
        //Готовим пустые данные для апдейта и проверяем
        ItemDto requestBody =
                new ItemDto(null, standardItem.getName().repeat(10), "", null, null, null);
        Long header = 1L;
        Long pathVar = 1L;

        RequestBuilder request = getRequest(requestBody, header, pathVar);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateWithToLongNewDescription() throws Exception {
        //Готовим пустые данные для апдейта и проверяем
        ItemDto requestBody =
                new ItemDto(null, standardItem.getName(), standardItem.getDescription().repeat(10), null, null, null);
        Long header = 1L;
        Long pathVar = 1L;

        RequestBuilder request = getRequest(requestBody, header, pathVar);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateWithInvalidItemId() throws Exception {
        Long header = 1L;
        Long pathVar = -1L;

        RequestBuilder request = getRequest(standardItem, header, pathVar);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void deleteItem_whenValidPathVariableAndHeader_thenDelete() {
        long header = 1L;
        long itemId = 1L;
        when(client.deleteItem(anyLong(), anyLong())).thenReturn(ResponseEntity.ok().body(standardItem));
        RequestBuilder request = MockMvcRequestBuilders
                .delete("/items/{itemId}", itemId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).deleteItem(header, itemId);
    }

    @Test
    public void shouldNotDeleteWithInvalidHeader() throws Exception {
        Long header = -1L;
        Long pathVar = 1L;

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/items/{itemId}", pathVar)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotDeleteWithInvalidPathVariable() throws Exception {
        Long header = 1L;
        Long pathVar = -1L;

        RequestBuilder request = MockMvcRequestBuilders
                .delete("/items/{itemId}", pathVar)
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
    public void getItemById_whenValidPathVariable_thenGet() {
        long itemId = 1L;
        when(client.getItem(anyLong())).thenReturn(ResponseEntity.ok().body(standardItem));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/items/{itemId}", itemId)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getItem(itemId);
    }

    @Test
    public void shouldNotGetWithInvalidPathVariable() throws Exception {
        Long pathVar = -1L;

        RequestBuilder request = MockMvcRequestBuilders
                .get("/items/{itemId}", pathVar)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @SneakyThrows
    @Test
    public void getAllItemsOfOwner_whenValidHeader_thenGet() {
        long header = 1L;
        when(client.getItems(anyLong())).thenReturn(ResponseEntity.ok().body(List.of()));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/items")
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getItems(header);
    }

    @SneakyThrows
    @Test
    public void getItemsByContext_AlwaysGet() {
        String text = "nothing";
        when(client.getItems(anyString())).thenReturn(ResponseEntity.ok().body(List.of()));
        RequestBuilder request = MockMvcRequestBuilders
                .get("/items/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("text", text)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1)).getItems(text);
    }

    @SneakyThrows
    @Test
    public void addNewComment_whenValidRequest_thenAdd() {
        long header = 1L;
        long itemId = 1L;
        CommentInputDto requestBody = new CommentInputDto(standardComment, null, null);
        when(client.addNewComment(anyLong(), anyLong(), any(CommentInputDto.class)))
                .thenReturn(ResponseEntity.ok().body(List.of()));

        RequestBuilder request = MockMvcRequestBuilders
                .post("/items/{itemId}/comment", itemId)
                .accept(MediaType.APPLICATION_JSON)
                .header("X-Sharer-User-Id", header)
                .content(objectMapper.writeValueAsString(requestBody))
                .contentType(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        assertNotNull(mvcResult.getResponse());
        verify(client, times(1))
                .addNewComment(header, itemId, requestBody);
    }

    @Test
    public void shouldNotCreateCommentWithInvalidHeader() throws Exception {
        Long header = -1L;
        Long pathVar = 1L;
        CommentInputDto requestBody = new CommentInputDto(standardComment, null, null);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/items/{itemId}/comment", pathVar)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateCommentWithInvalidPathVariable() throws Exception {
        Long header = 1L;
        Long pathVar = -1L;
        CommentInputDto requestBody = new CommentInputDto(standardComment, null, null);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/items/{itemId}/comment", pathVar)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateInvalidComment() throws Exception {
        Long header = 1L;
        Long pathVar = 1L;
        CommentInputDto requestBody = new CommentInputDto();
        RequestBuilder request = MockMvcRequestBuilders
                .post("/items/{itemId}/comment", pathVar)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateEmptyComment() throws Exception {
        Long header = 1L;
        Long pathVar = 1L;
        CommentInputDto requestBody = new CommentInputDto("   ", null, null);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/items/{itemId}/comment", pathVar)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateCommentWithToLongComment() throws Exception {
        Long header = 1L;
        Long pathVar = 1L;
        CommentInputDto requestBody = new CommentInputDto(standardComment.repeat(21), null, null);
        RequestBuilder request = MockMvcRequestBuilders
                .post("/items/{itemId}/comment", pathVar)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(MethodArgumentNotValidException.class, result.getResolvedException()))
                .andReturn();
    }

}
