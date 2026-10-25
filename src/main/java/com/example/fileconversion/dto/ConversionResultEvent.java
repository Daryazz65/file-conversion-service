package com.example.fileconversion.dto;

import com.example.fileconversion.domain.ConversionStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ConversionResultEvent(
        UUID eventId,
        UUID sourceEventId,
        ConversionStatus status,
        List<ConvertedFileDto> files,
        String errorMessage,
        Instant occurredAt
) {
}