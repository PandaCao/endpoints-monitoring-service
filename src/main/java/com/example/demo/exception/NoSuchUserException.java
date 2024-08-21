package com.example.demo.exception;

public class NoSuchUserException extends RuntimeException {

    public NoSuchUserException(String msg) {
        super(msg);
    }
}
