package com.example.fileconversion.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnTransformer;

@Getter
@Setter
@Entity
public class ConversionResult {

    @Id
    private UUID id;

    @Column(unique = true, nullable = false)
    private UUID sourceEventId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ConversionStatus status;

    @Column(columnDefinition = "jsonb", nullable = false)
    @ColumnTransformer(write = "?::jsonb")
    private String outputFiles;

    private String errorMessage;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected ConversionResult() {
    }

    public ConversionResult(
            UUID id,
            UUID sourceEventId,
            ConversionStatus status,
            String outputFiles,
            String errorMessage
    ) {
        this.id = id;
        this.sourceEventId = sourceEventId;
        this.status = status;
        this.outputFiles = outputFiles;
        this.errorMessage = errorMessage;
    }
}