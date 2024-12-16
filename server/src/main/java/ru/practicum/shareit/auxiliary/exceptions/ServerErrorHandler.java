package ru.practicum.shareit.auxiliary.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ServerErrorHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handlerMyValidation(final ValidationException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("\nValidation error - Class: {}; Method: {}; Line: {}; \nMessage: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put(e.getMessage(), e.getObjForBody());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<Map<String,Object>> handlerConflictValidation(final DuplicateDataException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("\nConflict error - Class: {}; Method: {}; Line: {}; \nMessage: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put(e.getMessage(), e.getObjForBody());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(AccessNotAllowedException.class)
    public ResponseEntity<Object> handlerAccessNotAllowed(final AccessNotAllowedException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("\nNotAllowed error - Class: {}; Method: {}; Line: {}; \nMessage: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put(e.getMessage(), e.getObjForBody());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handlerNotFound(final NotFoundException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("\nNotFound error - Class: {}; Method: {}; Line: {}; \nMessage: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put(e.getMessage(), e.getObjForBody());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleOtherException(final RuntimeException e) {
        log.error("Got 500 status Internal server error {}", e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
