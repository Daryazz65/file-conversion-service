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
public class OutboxMessage {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID aggregateId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @Column(nullable = false)
    private String topic;

    @Column(columnDefinition = "jsonb", nullable = false)
    @ColumnTransformer(write = "?::jsonb")
    private String payload;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant publishedAt;

    protected OutboxMessage() {
    }

    public OutboxMessage(
            UUID id,
            UUID aggregateId,
            EventType eventType,
            String topic,
            String payload
    ) {
        this.id = id;
        this.aggregateId = aggregateId;
        this.eventType = eventType;
        this.topic = topic;
        this.payload = payload;
    }

    public void markPublished() {
        publishedAt = Instant.now();
    }
}