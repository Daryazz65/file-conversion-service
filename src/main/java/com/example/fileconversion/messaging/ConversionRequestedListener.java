package com.example.fileconversion.messaging;

import com.example.fileconversion.dto.ConversionRequestedEvent;
import com.example.fileconversion.exception.EventDeserializationException;
import com.example.fileconversion.service.ConversionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ConversionRequestedListener {

    private final ConversionService service;
    private final ObjectMapper mapper;

    public ConversionRequestedListener(ConversionService service, ObjectMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @KafkaListener(topics = "${app.topics.conversion-requested}")
    public void consume(String payload) {
        try {
            ConversionRequestedEvent event = mapper.readValue(payload, ConversionRequestedEvent.class);
            service.handle(event);
        } catch (IOException exception) {
            throw new EventDeserializationException(exception);
        }
    }
}