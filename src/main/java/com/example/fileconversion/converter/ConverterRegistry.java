package com.example.fileconversion.converter;

import com.example.fileconversion.domain.FileType;
import com.example.fileconversion.exception.ConverterConfigurationException;
import java.util.List;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

@Component
public class ConverterRegistry {

    private final ObjectProvider<Converter> converters;

    public ConverterRegistry(ObjectProvider<Converter> converters) {
        this.converters = converters;
    }

    public List<ConvertedPdf> convert(String fileName, byte[] content) {
        FileType type = FileType.fromName(fileName);
        List<Converter> matches = findConverters(type);

        if (matches.size() != 1) {
            throw new ConverterConfigurationException(
                    "Expected exactly one converter for " + type + ", got " + matches.size()
            );
        }

        return matches.get(0).convert(fileName, content);
    }

    private List<Converter> findConverters(FileType type) {
        return converters.orderedStream()
                .filter(converter -> converter.supports(type))
                .toList();
    }
}