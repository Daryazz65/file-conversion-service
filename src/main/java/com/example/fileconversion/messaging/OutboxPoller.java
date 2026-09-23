package com.example.fileconversion.messaging;

import com.example.fileconversion.config.AppProperties;
import com.example.fileconversion.domain.OutboxMessage;
import com.example.fileconversion.exception.ConversionException;
import com.example.fileconversion.repository.OutboxMessageRepository;
import java.util.List;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class OutboxPoller {

    private static final int FIRST_PAGE = 0;

    private final OutboxMessageRepository outbox;
    private final KafkaTemplate<String, String> kafka;
    private final AppProperties properties;

    public OutboxPoller(
            OutboxMessageRepository outbox,
            KafkaTemplate<String, String> kafka,
            AppProperties properties
    ) {
        this.outbox = outbox;
        this.kafka = kafka;
        this.properties = properties;
    }

    @Scheduled(fixedDelayString = "${app.outbox.fixed-delay}")
    @SchedulerLock(
            name = "file-conversion-outbox",
            lockAtLeastFor = "PT1S",
            lockAtMostFor = "PT30S"
    )
    @Transactional
    public void publish() {
        List<OutboxMessage> messages = findUnpublishedMessages();

        for (OutboxMessage message : messages) {
            publish(message);
        }
    }

    private List<OutboxMessage> findUnpublishedMessages() {
        return outbox.findUnpublishedForUpdate(
                PageRequest.of(FIRST_PAGE, properties.outbox().batchSize())
        );
    }

    private void publish(OutboxMessage message) {
        try {
            kafka.send(
                    message.getTopic(),
                    message.getId().toString(),
                    message.getPayload()
            ).get();

            message.markPublished();
        } catch (Exception exception) {
            throw new ConversionException(
                    "Cannot publish outbox message " + message.getId(),
                    exception
            );
        }
    }
}