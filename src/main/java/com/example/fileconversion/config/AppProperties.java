package com.example.fileconversion.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public record AppProperties(
        Minio minio,
        Topics topics,
        Outbox outbox
) {

    public record Minio(
            String endpoint,
            String accessKey,
            String secretKey,
            String bucket
    ) {
    }

    public record Topics(
            String conversionRequested,
            String conversionResult
    ) {
    }

    public record Outbox(
            long fixedDelay,
            int batchSize
    ) {
    }
}