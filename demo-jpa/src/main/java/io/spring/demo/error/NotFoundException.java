package io.spring.demo.error;

public class NotFoundException extends RuntimeException {

    public NotFoundException(String message, Object...args) {
        super(message.formatted(args));
    }
}