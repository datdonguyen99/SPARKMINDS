package net.sparkminds.librarymanagement.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@AllArgsConstructor
@Getter
@Builder
public class ErrorResponse {
    private final String message;

    private final String messageCode;

    private final int statusCode;

    private final Date timestamp;
}
