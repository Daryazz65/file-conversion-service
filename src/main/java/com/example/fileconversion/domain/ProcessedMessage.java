package com.example.fileconversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;

@Getter
@Entity
public class ProcessedMessage {

    @Id
    private UUID id;

    @Column(nullable = false)
    private Instant processedAt = Instant.now();

    protected ProcessedMessage() {
    }

    public ProcessedMessage(UUID id) {
        this.id = id;
    }
}