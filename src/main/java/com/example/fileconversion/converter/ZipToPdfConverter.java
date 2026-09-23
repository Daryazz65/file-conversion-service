package com.example.fileconversion.converter;

import com.example.fileconversion.domain.FileType;
import com.example.fileconversion.exception.ConversionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.springframework.stereotype.Component;

@Component
public class ZipToPdfConverter implements Converter {

    private final ConverterRegistry registry;

    public ZipToPdfConverter(ConverterRegistry registry) {
        this.registry = registry;
    }

    @Override
    public boolean supports(FileType type) {
        return type == FileType.ZIP;
    }

    @Override
    public List<ConvertedPdf> convert(String name, byte[] content) {
        try (ZipInputStream zip = new ZipInputStream(new ByteArrayInputStream(content))) {
            return convertEntries(zip);
        } catch (IOException exception) {
            throw new ConversionException("Cannot unpack archive " + name, exception);
        }
    }

    private List<ConvertedPdf> convertEntries(ZipInputStream zip) throws IOException {
        List<ConvertedPdf> result = new ArrayList<>();
        ZipEntry entry;

        while ((entry = zip.getNextEntry()) != null) {
            if (!entry.isDirectory()) {
                result.addAll(convertEntry(zip, entry));
            }

            zip.closeEntry();
        }

        return result;
    }

    private List<ConvertedPdf> convertEntry(ZipInputStream zip, ZipEntry entry) throws IOException {
        byte[] content = zip.readAllBytes();
        return registry.convert(entry.getName(), content);
    }
}