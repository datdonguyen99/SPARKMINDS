package net.sparkminds.librarymanagement.exception;

import lombok.Getter;

@Getter
public class ErrorDetails {
    private final String message;
    private final String messageCode;

    public ErrorDetails(String message, String messageCode) {
        super();
        this.message = message;
        this.messageCode = messageCode;
    }
}
