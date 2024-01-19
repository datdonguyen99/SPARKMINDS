package net.sparkminds.librarymanagement.exception;

import lombok.Getter;

@Getter
public class ResourceUnauthorizedException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private final String msgCode;

    public ResourceUnauthorizedException(String msg, String msgCode) {
        super(msg);
        this.msgCode = msgCode;
    }
}
