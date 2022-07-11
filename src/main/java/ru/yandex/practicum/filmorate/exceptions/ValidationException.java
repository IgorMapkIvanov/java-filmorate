package ru.yandex.practicum.filmorate.exceptions;

public class ValidationException extends RuntimeException {
    private String massage;

    public ValidationException(String massage) {
        super(massage);
    }

    public String getMassage() {
        return massage;
    }
}