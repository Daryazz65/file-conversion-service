package com.example.fileconversion.domain;

import com.example.fileconversion.exception.UnsupportedFileTypeException;
import java.util.Locale;

public enum FileType {

    TXT,
    PNG,
    JPG,
    ZIP;

    public static FileType fromName(String name) {
        String extension = extractExtension(name);

        try {
            return valueOf(normalizeExtension(extension));
        } catch (IllegalArgumentException exception) {
            throw new UnsupportedFileTypeException(name, exception);
        }
    }

    private static String extractExtension(String name) {
        int dotIndex = name.lastIndexOf('.');

        if (dotIndex < 0 || dotIndex == name.length() - 1) {
            throw new UnsupportedFileTypeException(name);
        }

        return name.substring(dotIndex + 1);
    }

    private static String normalizeExtension(String extension) {
        return extension.toUpperCase(Locale.ROOT)
                .replace("JPEG", "JPG");
    }
}