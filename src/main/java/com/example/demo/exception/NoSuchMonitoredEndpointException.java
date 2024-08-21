package com.example.demo.exception;

public class NoSuchMonitoredEndpointException extends RuntimeException {

    public NoSuchMonitoredEndpointException(String msg) {
        super(msg);
    }
}
