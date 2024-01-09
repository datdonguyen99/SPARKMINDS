package net.sparkminds.librarymanagement.exception;

import org.springframework.http.HttpStatus;

public class LibraryManagementException extends RuntimeException {
    private final HttpStatus status;

    private final String message;

    public LibraryManagementException(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
