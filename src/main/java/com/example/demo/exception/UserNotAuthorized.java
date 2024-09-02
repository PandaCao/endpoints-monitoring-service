package com.example.demo.exception;

public class UserNotAuthorized extends RuntimeException {

    public UserNotAuthorized(String msg) {
        super(msg);
    }
}
