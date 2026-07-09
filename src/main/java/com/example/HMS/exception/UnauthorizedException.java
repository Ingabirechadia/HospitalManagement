package com.example.HMS.exception;

import lombok.Getter;

@Getter
public class UnauthorizedException extends RuntimeException {

    private final String code;

    public UnauthorizedException(String message) {
        super(message);
        this.code = "UNAUTHORIZED";
    }

    public UnauthorizedException(String message, String code) {
        super(message);
        this.code = code;
    }
}