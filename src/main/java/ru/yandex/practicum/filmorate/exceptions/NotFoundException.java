package ru.yandex.practicum.filmorate.exceptions;

public class NotFoundException extends RuntimeException {
    private String massage;

    public NotFoundException(String massage) {
        super(massage);
    }

    public String getMassage() {
        return massage;
    }
}
