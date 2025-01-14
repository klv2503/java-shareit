package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.auxiliary.exceptions.*;
import ru.practicum.shareit.item.dto.comments.CommentInputDto;
import ru.practicum.shareit.item.dto.items.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ExtendWith(MockitoExtension.class)
public class ServiceErrorHandlerTests {
    @InjectMocks
    private ServerErrorHandler errorHandler; // Тестируемый хендлер

    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

    @Test
    public void testHandlerMyValidationIsCalled() {
        // Данные для теста
        CommentInputDto obj = CommentInputDto.builder().text("testing").build();
        String message = "User not used this item. Comment is prohibited";
        ValidationException exception = new ValidationException(message, obj);

        // Настраиваем мок для выброса исключения
        doThrow(exception).when(itemService).addNewComment(obj);

        // Эмуляция вызова хендлера
        ResponseEntity<ErrorResponse> response = null;
        try {
            itemService.addNewComment(obj);
        } catch (ValidationException e) {
            response = errorHandler.handlerMyValidation(e);
        }

        // Проверка ответа
        ErrorResponse body = response.getBody();

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(body);
        assertEquals("User not used this item. Comment is prohibited", body.getMessage());
        assertEquals(obj, body.getFailedObject());

        verify(itemService).addNewComment(obj);
    }

    @Test
    public void testHandlerConflictValidationIsCalled() {
        // Данные для теста
        UserDto obj = new UserDto("name", "email@email.net");
        String message = "Users e-mail already exists in base";
        DuplicateDataException exception = new DuplicateDataException(message, obj);

        doThrow(exception).when(userService).createUser(obj);

        ResponseEntity<ErrorResponse> response = null;
        try {
            userService.createUser(obj);
        } catch (ValidationException e) {
            response = errorHandler.handlerMyValidation(e);
        }

        // Проверка ответа
        ErrorResponse body = response.getBody();

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(body);
        assertEquals("Users e-mail already exists in base", body.getMessage());
        assertEquals(obj, body.getFailedObject());

        verify(userService).createUser(obj);
    }

    @Test
    public void testHandlerAccessNotAllowedIsCalled() {
        // Данные для теста
        ItemDto obj = ItemDto.builder()
                .id(1L)
                .name("nothing")
                .description("something")
                .available(true)
                .owner(1L)
                .requestId(null)
                .build();
        String message = "Changing of item is forbidden";

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenThrow(new AccessNotAllowedException(message, obj));

        // Эмуляция вызова хендлера
        ResponseEntity<ErrorResponse> response = null;
        try {
            itemService.updateItem(1L, 1L, obj);
        } catch (AccessNotAllowedException e) {
            response = errorHandler.handlerAccessNotAllowed(e);
        }

        // Проверка ответа
        ErrorResponse body = response.getBody();

        assertEquals(403, response.getStatusCodeValue());
        assertNotNull(body);
        assertEquals("Changing of item is forbidden", body.getMessage());
        assertEquals(obj, body.getFailedObject());

        verify(itemService).updateItem(1L, 1L, obj);
    }

    @Test
    public void testHandlerNotFoundIsCalled() {
        // Данные для теста
        ItemDto obj = ItemDto.builder()
                .id(1L)
                .name("nothing")
                .description("something")
                .available(true)
                .owner(1L)
                .requestId(null)
                .build();
        String message = "Item not found";

        when(itemService.updateItem(anyLong(), anyLong(), any(ItemDto.class)))
                .thenThrow(new NotFoundException(message, obj));

        // Эмуляция вызова хендлера
        ResponseEntity<ErrorResponse> response = null;
        try {
            itemService.updateItem(1L, 1L, obj);
        } catch (NotFoundException e) {
            response = errorHandler.handlerNotFound(e);
        }

        // Проверка ответа
        ErrorResponse body = response.getBody();

        assertEquals(404, response.getStatusCodeValue());
        assertNotNull(body);
        assertEquals("Item not found", body.getMessage());
        assertEquals(obj, body.getFailedObject());

        verify(itemService).updateItem(1L, 1L, obj);
    }

    @Test
    public void testHandlerOtherExceptionIsCalled() {
        String errorMessage = "Internal server error";
        ErrorResponse mustBeResponseBody = ErrorResponse.builder()
                .message(errorMessage)
                .failedObject(null)
                .build();

        when(itemService.getItem(anyLong()))
                .thenThrow(new RuntimeException(errorMessage));

        // Эмуляция вызова хендлера
        ResponseEntity<ErrorResponse> response = null;
        try {
            itemService.getItem(1L);
        } catch (RuntimeException e) {
            response = errorHandler.handlerOtherException(e);
        }

        // Проверка ответа
        assertEquals(500, response.getStatusCodeValue());
        assertEquals(mustBeResponseBody, response.getBody());

        verify(itemService).getItem(1L);
    }

}
