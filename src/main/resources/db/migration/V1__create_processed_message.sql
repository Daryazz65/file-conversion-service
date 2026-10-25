CREATE TABLE processed_message (
                                   id UUID PRIMARY KEY,
                                   processed_at TIMESTAMPTZ NOT NULL DEFAULT now()
);