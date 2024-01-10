package net.sparkminds.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
    public GlobalExceptionHandler() {
        super();
    }

    // 500
    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleInternal(final RuntimeException runtimeException) {
        logger.error("500 status code", runtimeException);
        final ErrorDetails errorDetails = new ErrorDetails("InternalError", runtimeException.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 409
    @ExceptionHandler({UserAlreadyExistException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Object> handleUserAlreadyExist(final RuntimeException runtimeException) {
        logger.error("409 status code", runtimeException);
        final ErrorDetails errorDetails = new ErrorDetails("User Already Exist!", runtimeException.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.CONFLICT);
    }

    // 404
    @ExceptionHandler({UserNotFoundException.class})
    public ResponseEntity<Object> handleUserNotFound(final RuntimeException runtimeException) {
        logger.error("404 status code", runtimeException);
        final ErrorDetails errorDetails = new ErrorDetails("User Not Found!", runtimeException.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }
}
