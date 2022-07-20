package ru.yandex.practicum.filmorate.exception;

public class NotFoundException extends RuntimeException {
    String message;
    public NotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
