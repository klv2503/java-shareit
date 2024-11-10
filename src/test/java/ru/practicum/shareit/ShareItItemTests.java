package ru.practicum.shareit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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
import ru.practicum.shareit.exceptions.AccessNotAllowedException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemMemoryStorage;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMemoryStorage;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
@RequiredArgsConstructor
class ShareItItemTests {

    private final ItemDto standardItem = new ItemDto(null, "First item",
            "Abcdefgh Abcdefgh Abcdefgh", true, null, null);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ItemController itemController;

    @Autowired
    private UserMemoryStorage userStorage;

    @Autowired
    private ItemMemoryStorage itemStorage;

    @Autowired
    private ItemMapper itemMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public void addUsersForTests() {
        Map<Long, User> initUsers = new HashMap<>();
        initUsers.put(1L, new User(1L, "First user", "first@nowhere.net"));
        initUsers.put(2L, new User(2L, "Second user", "second@anyway.com"));
        userStorage.setUsers(initUsers);
    }

    @AfterEach
    public void clearItems() {
        Map<Long, Item> noItems = new HashMap<>();
        itemStorage.setItems(noItems);
    }

    public void addItemForTests() {
        Map<Long, Item> singleItem = new HashMap<>();
        Item item = itemMapper.mapItemDtoToItem(standardItem);
        item.setId(1L);
        item.setOwner(1L);
        singleItem.put(1L, item);
        itemStorage.setItems(singleItem);
    }

    //Вспомогательный метод для реквестов create
    public RequestBuilder getRequest(ItemDto requestBody, Long header) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .post("/items")
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
    }

    //Вспомогательный метод для реквестов update
    public RequestBuilder getRequest(String searchParam) {
        return MockMvcRequestBuilders
                .get("/items/search")
                .accept(MediaType.APPLICATION_JSON)
                .param("text", searchParam);
    }

    //Вспомогательный метод для реквестов update
    public RequestBuilder getRequest(ItemDto requestBody, Long header, Long pathVar) throws JsonProcessingException {
        return MockMvcRequestBuilders
                .patch("/items/{itemId}", pathVar)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestBody))
                .header("X-Sharer-User-Id", header)
                .contentType(MediaType.APPLICATION_JSON);
    }

    //Creation tests
    @Test
    public void shouldCorrectlyCreateNewItem() throws Exception {
        // Готовим входные данные для вызова метода
        Long header = 1L;

        RequestBuilder request = getRequest(standardItem, header);

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ItemDto responseBody = objectMapper.readValue(response, ItemDto.class);
        assertNotNull(responseBody);
        assertEquals(1L, responseBody.getId());
        assertEquals("First item", responseBody.getName());
        assertEquals("Abcdefgh Abcdefgh Abcdefgh", responseBody.getDescription());
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
    public void shouldNotCreateWithInvalidDescription() throws Exception {
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

    @Test
    public void shouldNotCreateWithInvalidHeader() throws Exception {
        //Поскольку @RequestHeader во всех методах контроллера ItemController аннотирован одинаково,
        // этот тест подходит и для других методов
        //Проверяем ситуацию header < 0
        Long header = -1L;
        RequestBuilder request = getRequest(standardItem, header);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotCreateWithNotExistedUser() throws Exception {
        //Проверяем ситуацию header > maximalUsersId
        Long header = 100L;
        RequestBuilder request = getRequest(standardItem, header);
        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isNotFound())
                .andExpect(result ->
                        assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andReturn();
    }
    //End of creation tests

    //Updating tests
    @Test
    public void shouldUpdateExistedItem() throws Exception {
        //Готовим корректные данные для апдейта и проверяем
        addItemForTests();
        ItemDto requestBody =
                new ItemDto(null, "Superitem", "Abcdefgh", false, null, null);
        Long header = 1L;
        Long pathVar = 1L;

        RequestBuilder request = getRequest(requestBody, header, pathVar);

        MvcResult mvcResult = mockMvc.perform(request)
                .andExpect(status().isOk()).andReturn();

        String response = mvcResult.getResponse().getContentAsString();
        ItemDto responseBody = objectMapper.readValue(response, ItemDto.class);
        assertNotNull(responseBody);
        assertEquals(1L, responseBody.getId());
        assertEquals("Superitem", responseBody.getName());
        assertEquals("Abcdefgh", responseBody.getDescription());
        assertEquals(false, responseBody.getAvailable());
    }

    @Test
    public void shouldNotUpdateWithEmptyBody() throws Exception {
        //Готовим пустые данные для апдейта и проверяем
        addItemForTests();
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
    public void shouldNotUpdateNotExistedItem() throws Exception {
        //Готовим корректные данные для апдейта несуществующего item и проверяем
        addItemForTests();
        ItemDto requestBody =
                new ItemDto(null, "Superitem", "Abcdefgh", false, null, null);
        Long header = 1L;
        Long pathVar = 100L;

        RequestBuilder request = getRequest(requestBody, header, pathVar);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isNotFound())
                .andExpect(result ->
                        assertInstanceOf(NotFoundException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateAlienItem() throws Exception {
        //Готовим корректные данные для апдейта существующего item
        // в заголовке указан существующий user, но не owner и проверяем
        addItemForTests();
        ItemDto requestBody =
                new ItemDto(null, "Superitem", "Abcdefgh", false, null, null);
        Long header = 2L;
        Long pathVar = 1L;

        RequestBuilder request = getRequest(requestBody, header, pathVar);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isForbidden())
                .andExpect(result ->
                        assertInstanceOf(AccessNotAllowedException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    public void shouldNotUpdateWithInvalidItemId() throws Exception {
        //Готовим корректные данные для апдейта несуществующего item и проверяем при некорректном @PathVariable
        //@PathVariable в ItemController везде аннотирована одинаково, проверка подходит и для других методов
        addItemForTests();
        ItemDto requestBody =
                new ItemDto(null, "Superitem", "Abcdefgh", false, null, null);
        Long header = 1L;
        Long pathVar = -1L;

        RequestBuilder request = getRequest(requestBody, header, pathVar);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isBadRequest())
                .andExpect(result ->
                        assertInstanceOf(ConstraintViolationException.class, result.getResolvedException()))
                .andReturn();
    }

    //End of updating tests
    //Testes of search
    @Test
    public void shouldFoundItemByNameContext() throws Exception {
        addItemForTests();
        String forSearch = "iT"; //контекст есть в name, отсутствует в description

        RequestBuilder request = getRequest(forSearch);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ItemDto> responseBody = objectMapper.readValue(response, new TypeReference<>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
    }

    @Test
    public void shouldFoundItemByDescriptionContext() throws Exception {
        addItemForTests();
        String forSearch = "EfG"; //контекст есть в description, отсутствует в name

        RequestBuilder request = getRequest(forSearch);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ItemDto> responseBody = objectMapper.readValue(response, new TypeReference<>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        assertNotNull(responseBody);
        assertEquals(1, responseBody.size());
    }

    @Test
    public void shouldNotFoundContext() throws Exception {
        addItemForTests();
        String forSearch = "ZzZz";

        RequestBuilder request = getRequest(forSearch);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ItemDto> responseBody = objectMapper.readValue(response, new TypeReference<>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        assertNotNull(responseBody);
        assertEquals(0, responseBody.size());
    }

    @Test
    public void shouldGiveEmptyListWithEmptyContext() throws Exception {
        addItemForTests();
        String forSearch = "";

        RequestBuilder request = getRequest(forSearch);

        MvcResult mvcResult = mockMvc.perform(request).andExpect(status().isOk()).andReturn();
        String response = mvcResult.getResponse().getContentAsString();
        List<ItemDto> responseBody = objectMapper.readValue(response, new TypeReference<>() {
            @Override
            public Type getType() {
                return super.getType();
            }
        });
        assertNotNull(responseBody);
        assertEquals(0, responseBody.size());
    }

}
