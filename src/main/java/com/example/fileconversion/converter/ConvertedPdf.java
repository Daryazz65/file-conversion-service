package com.example.fileconversion.converter;

public record ConvertedPdf(
        String sourceName,
        byte[] content ) { }