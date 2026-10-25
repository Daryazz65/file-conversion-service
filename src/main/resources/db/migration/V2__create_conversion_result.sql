CREATE TABLE conversion_result (
                                   id UUID PRIMARY KEY,
                                   source_event_id UUID NOT NULL UNIQUE,
                                   status VARCHAR(32) NOT NULL,
                                   output_files JSONB NOT NULL,
                                   error_message TEXT,
                                   created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);