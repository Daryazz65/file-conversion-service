package com.example.fileconversion.dto;

import java.util.UUID;

public record ConversionRequestedEvent(
        UUID eventId,
        String bucket,
        String objectKey,
        String fileName
) {
}