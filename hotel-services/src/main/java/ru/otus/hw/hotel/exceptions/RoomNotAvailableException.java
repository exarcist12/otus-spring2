package ru.otus.hw.hotel.exceptions;

public class RoomNotAvailableException extends RuntimeException {

    public RoomNotAvailableException(String message) {
        super(message);
    }
}