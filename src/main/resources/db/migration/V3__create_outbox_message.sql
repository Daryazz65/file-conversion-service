CREATE TABLE outbox_message (
                                id UUID PRIMARY KEY,
                                aggregate_id UUID NOT NULL,
                                event_type VARCHAR(64) NOT NULL,
                                topic VARCHAR(255) NOT NULL,
                                payload JSONB NOT NULL,
                                created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
                                published_at TIMESTAMPTZ
);

CREATE INDEX idx_outbox_unpublished
    ON outbox_message (created_at)
    WHERE published_at IS NULL;