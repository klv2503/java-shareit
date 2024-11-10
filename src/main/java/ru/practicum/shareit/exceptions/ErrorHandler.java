package ru.practicum.shareit.exceptions;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handlerMyValidation(final ValidationException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("\nValidation error - Class: {}; Method: {}; Line: {}; \nMessage: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        return new ResponseEntity<>(e.getObjForBody(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handlerNumberFormatException(final NumberFormatException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("\nMethodArgumentTypeMismatchException error - Class: {}; Method: {}; Line: {}; \nMessage: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handlerConstraintViolationException(final ConstraintViolationException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("\nConstraintViolationException error - Class: {}; Method: {}; Line: {}; \nMessage: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
    }

    @ExceptionHandler(DuplicateDataException.class)
    public ResponseEntity<Object> handlerConflictValidation(final DuplicateDataException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("\nConflict error - Class: {}; Method: {}; Line: {}; \nMessage: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        return new ResponseEntity<>(e.getObjForBody(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(AccessNotAllowedException.class)
    public ResponseEntity<Object> handlerAccessNotAllowed(final AccessNotAllowedException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("\nNotAllowed error - Class: {}; Method: {}; Line: {}; \nMessage: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handlerNotFound(final NotFoundException e) {
        StackTraceElement sElem = e.getStackTrace()[0];
        String className = sElem.getClassName();
        String str = className.substring(className.lastIndexOf(".") + 1);
        log.info("\nNotFound error - Class: {}; Method: {}; Line: {}; \nMessage: {}",
                str, sElem.getMethodName(), sElem.getLineNumber(), e.getMessage());
        return new ResponseEntity<>(e.getObjForBody(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<Object> handleOtherException(final RuntimeException e) {
        log.error("Got 500 status Internal server error {}", e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
