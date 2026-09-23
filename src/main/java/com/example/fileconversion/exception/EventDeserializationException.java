package com.example.fileconversion.exception;

public class EventDeserializationException extends ConversionException {

    public EventDeserializationException(Throwable cause) {
        super("Cannot deserialize conversion event", cause);
    }
}