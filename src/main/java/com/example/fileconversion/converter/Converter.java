package com.example.fileconversion.converter;

import com.example.fileconversion.domain.FileType;
import java.util.List;

public interface Converter {

    boolean supports(FileType type);

    List<ConvertedPdf> convert(String fileName, byte[] content); }