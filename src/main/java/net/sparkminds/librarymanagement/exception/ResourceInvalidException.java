package net.sparkminds.librarymanagement.exception;

import lombok.Getter;

@Getter
public class ResourceInvalidException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private final String msgCode;

    public ResourceInvalidException(String msg, String msgCode) {
        super(msg);
        this.msgCode = msgCode;
    }
}
