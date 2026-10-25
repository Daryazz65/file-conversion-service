package com.example.fileconversion.converter;

import com.example.fileconversion.domain.FileType;
import com.example.fileconversion.exception.ConversionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName;
import org.springframework.stereotype.Component;

@Component
public class TextToPdfConverter implements Converter {

    private static final float START_X = 50;
    private static final float START_Y = 750;
    private static final float FONT_SIZE = 11;
    private static final float LINE_HEIGHT = 14;
    private static final String UNSUPPORTED_CHARACTER_REPLACEMENT = "?";
    private static final String ASCII_PRINTABLE_CHARACTERS_REGEX = "[^\\x20-\\x7E]";

    @Override
    public boolean supports(FileType type) {
        return type == FileType.TXT;
    }

    @Override
    public List<ConvertedPdf> convert(String name, byte[] content) {
        try (
                PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            String[] lines = readLines(content);

            PDPage page = new PDPage();
            document.addPage(page);

            writeLines(document, page, lines);
            document.save(output);

            return List.of(new ConvertedPdf(name, output.toByteArray()));
        } catch (IOException exception) {
            throw new ConversionException("Cannot convert text file " + name, exception);
        }
    }

    private String[] readLines(byte[] content) {
        return new String(content, StandardCharsets.UTF_8).split("\\R", -1);
    }

    private void writeLines(PDDocument document, PDPage page, String[] lines) throws IOException {
        try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
            stream.beginText();
            stream.setFont(new PDType1Font(FontName.HELVETICA), FONT_SIZE);
            stream.setLeading(LINE_HEIGHT);
            stream.newLineAtOffset(START_X, START_Y);

            for (String line : lines) {
                stream.showText(sanitize(line));
                stream.newLine();
            }

            stream.endText();
        }
    }

    private String sanitize(String line) {
        return line.replaceAll(
                ASCII_PRINTABLE_CHARACTERS_REGEX,
                UNSUPPORTED_CHARACTER_REPLACEMENT
        );
    }
}