package com.example.fileconversion.converter;

import com.example.fileconversion.domain.FileType;
import com.example.fileconversion.exception.ConversionException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.springframework.stereotype.Component;

@Component
public class ImageToPdfConverter implements Converter {

    private static final float PAGE_WIDTH = 612;
    private static final float PAGE_HEIGHT = 792;
    private static final float MAX_IMAGE_WIDTH = 500;
    private static final float MAX_IMAGE_HEIGHT = 700;

    @Override
    public boolean supports(FileType type) {
        return type == FileType.PNG || type == FileType.JPG;
    }

    @Override
    public List<ConvertedPdf> convert(String name, byte[] content) {
        try (
                PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()
        ) {
            PDImageXObject image = PDImageXObject.createFromByteArray(document, content, name);
            PDPage page = new PDPage();

            document.addPage(page);

            drawImage(document, page, image);
            document.save(output);

            return List.of(new ConvertedPdf(name, output.toByteArray()));
        } catch (IOException exception) {
            throw new ConversionException("Cannot convert image " + name, exception);
        }
    }

    private void drawImage(PDDocument document, PDPage page, PDImageXObject image) throws IOException {
        float scale = calculateScale(image);
        float width = image.getWidth() * scale;
        float height = image.getHeight() * scale;
        float x = (PAGE_WIDTH - width) / 2;
        float y = (PAGE_HEIGHT - height) / 2;

        try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
            stream.drawImage(image, x, y, width, height);
        }
    }

    private float calculateScale(PDImageXObject image) {
        return Math.min(
                MAX_IMAGE_WIDTH / image.getWidth(),
                MAX_IMAGE_HEIGHT / image.getHeight()
        );
    }
}