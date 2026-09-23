package com.example.fileconversion.service;

import com.example.fileconversion.config.AppProperties;
import com.example.fileconversion.converter.ConvertedPdf;
import com.example.fileconversion.converter.ConverterRegistry;
import com.example.fileconversion.domain.ConversionResult;
import com.example.fileconversion.domain.ConversionStatus;
import com.example.fileconversion.domain.EventType;
import com.example.fileconversion.domain.OutboxMessage;
import com.example.fileconversion.domain.ProcessedMessage;
import com.example.fileconversion.dto.ConversionRequestedEvent;
import com.example.fileconversion.dto.ConversionResultEvent;
import com.example.fileconversion.dto.ConvertedFileDto;
import com.example.fileconversion.exception.ConversionException;
import com.example.fileconversion.repository.ConversionResultRepository;
import com.example.fileconversion.repository.OutboxMessageRepository;
import com.example.fileconversion.repository.ProcessedMessageRepository;
import com.example.fileconversion.storage.ObjectStorage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConversionService {

    private final ProcessedMessageRepository inbox;
    private final ConversionResultRepository results;
    private final OutboxMessageRepository outbox;
    private final ObjectStorage storage;
    private final ConverterRegistry converters;
    private final ObjectMapper mapper;
    private final AppProperties properties;

    public ConversionService(
            ProcessedMessageRepository inbox,
            ConversionResultRepository results,
            OutboxMessageRepository outbox,
            ObjectStorage storage,
            ConverterRegistry converters,
            ObjectMapper mapper,
            AppProperties properties
    ) {
        this.inbox = inbox;
        this.results = results;
        this.outbox = outbox;
        this.storage = storage;
        this.converters = converters;
        this.mapper = mapper;
        this.properties = properties;
    }

    @Transactional
    public void handle(ConversionRequestedEvent request) {
        if (inbox.existsById(request.eventId())) {
            return;
        }

        try {
            List<ConvertedFileDto> files = convertAndUpload(request);
            saveResultAndOutbox(request.eventId(), ConversionStatus.SUCCESS, files, null);
        } catch (ConversionException exception) {
            saveResultAndOutbox(
                    request.eventId(),
                    ConversionStatus.ERROR,
                    List.of(),
                    exception.getMessage()
            );
        }

        inbox.save(new ProcessedMessage(request.eventId()));
    }

    private List<ConvertedFileDto> convertAndUpload(ConversionRequestedEvent request) {
        byte[] sourceFile = storage.download(request.bucket(), request.objectKey());
        List<ConvertedPdf> pdfs = converters.convert(request.fileName(), sourceFile);

        List<ConvertedFileDto> files = new ArrayList<>();

        for (int index = 0; index < pdfs.size(); index++) {
            ConvertedPdf pdf = pdfs.get(index);
            String objectKey = buildConvertedObjectKey(request.eventId(), index + 1, pdf.sourceName());

            storage.upload(
                    properties.minio().bucket(),
                    objectKey,
                    pdf.content(),
                    "application/pdf"
            );

            files.add(new ConvertedFileDto(
                    properties.minio().bucket(),
                    objectKey,
                    pdf.sourceName()
            ));
        }

        return files;
    }

    private void saveResultAndOutbox(
            UUID sourceEventId,
            ConversionStatus status,
            List<ConvertedFileDto> files,
            String errorMessage
    ) {
        try {
            UUID resultId = UUID.randomUUID();
            String filesJson = mapper.writeValueAsString(files);

            results.save(new ConversionResult(
                    resultId,
                    sourceEventId,
                    status,
                    filesJson,
                    errorMessage
            ));

            ConversionResultEvent event = new ConversionResultEvent(
                    UUID.randomUUID(),
                    sourceEventId,
                    status,
                    files,
                    errorMessage,
                    Instant.now()
            );

            outbox.save(new OutboxMessage(
                    event.eventId(),
                    resultId,
                    resolveEventType(status),
                    properties.topics().conversionResult(),
                    mapper.writeValueAsString(event)
            ));
        } catch (JsonProcessingException exception) {
            throw new ConversionException("Cannot serialize conversion result", exception);
        }
    }

    private EventType resolveEventType(ConversionStatus status) {
        return status == ConversionStatus.SUCCESS
                ? EventType.CONVERSION_COMPLETED
                : EventType.CONVERSION_FAILED;
    }

    private String buildConvertedObjectKey(UUID eventId, int index, String sourceName) {
        return "converted/"
                + eventId
                + "/"
                + index
                + "-"
                + withoutExtension(sourceName)
                + ".pdf";
    }

    private String withoutExtension(String name) {
        int dotIndex = name.lastIndexOf('.');
        return dotIndex < 0 ? name : name.substring(0, dotIndex);
    }
}