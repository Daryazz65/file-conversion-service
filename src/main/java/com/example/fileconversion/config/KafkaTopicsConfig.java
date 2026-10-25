package com.example.fileconversion.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicsConfig {

    private static final int PARTITIONS = 3;
    private static final int REPLICAS = 1;

    @Bean
    public NewTopic conversionRequestedTopic(AppProperties properties) {
        return TopicBuilder.name(properties.topics().conversionRequested())
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .build();
    }

    @Bean
    public NewTopic conversionResultTopic(AppProperties properties) {
        return TopicBuilder.name(properties.topics().conversionResult())
                .partitions(PARTITIONS)
                .replicas(REPLICAS)
                .build();
    }
}