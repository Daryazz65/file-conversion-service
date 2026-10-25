package com.example.fileconversion.dto;

public record ConvertedFileDto(
        String bucket,
        String objectKey,
        String originalFileName
) {
}