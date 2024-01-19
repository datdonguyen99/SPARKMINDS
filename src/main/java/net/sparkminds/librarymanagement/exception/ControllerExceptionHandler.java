package net.sparkminds.librarymanagement.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Date;

@ControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse message = ErrorResponse.builder()
                .message(ex.getMessage())
                .messageCode(ex.getMsgCode())
                .statusCode(HttpStatus.NOT_FOUND.value())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceInvalidException.class)
    public ResponseEntity<ErrorResponse> resourceInvalidException(ResourceInvalidException ex) {
        ErrorResponse message = ErrorResponse.builder()
                .message(ex.getMessage())
                .messageCode(ex.getMsgCode())
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceUnauthorizedException.class)
    public ResponseEntity<ErrorResponse> resourceUnauthorizedException(ResourceUnauthorizedException ex) {
        ErrorResponse message = ErrorResponse.builder()
                .message(ex.getMessage())
                .messageCode(ex.getMsgCode())
                .statusCode(HttpStatus.UNAUTHORIZED.value())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(message, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(ResourceForbiddenException.class)
    public ResponseEntity<ErrorResponse> resourceForbiddenException(ResourceForbiddenException ex) {
        ErrorResponse message = ErrorResponse.builder()
                .message(ex.getMessage())
                .messageCode(ex.getMsgCode())
                .statusCode(HttpStatus.FORBIDDEN.value())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(message, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponse message = ErrorResponse.builder()
                .message(ex.getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                        .orElse("Validation failed"))
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> globalExceptionHandler(Exception ex) {
        ErrorResponse message = ErrorResponse.builder()
                .message(ex.getMessage())
                .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .timestamp(new Date())
                .build();

        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
