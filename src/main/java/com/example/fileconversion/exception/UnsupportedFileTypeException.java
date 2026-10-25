package com.example.fileconversion.exception;

public class UnsupportedFileTypeException extends ConversionException {

    public UnsupportedFileTypeException(String fileName) {
        super("Unsupported file type: " + fileName);
    }

    public UnsupportedFileTypeException(String fileName, Throwable cause) {
        super("Unsupported file type: " + fileName, cause);
    }
}