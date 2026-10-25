package com.example.fileconversion;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.fileconversion.repository.OutboxMessageRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class InfrastructureIT extends AbstractIT {

    @Autowired
    private OutboxMessageRepository outbox;

    @Test
    void startsContainersAndAppliesMigrations() {
        assertThat(outbox.count()).isZero();
    }
}