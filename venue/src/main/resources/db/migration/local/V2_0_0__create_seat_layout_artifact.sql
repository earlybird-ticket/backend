CREATE TABLE p_seat_layout_artifact
(
    id                  UUID PRIMARY KEY,
    concert_id          UUID         NOT NULL,
    concert_sequence_id UUID         NOT NULL,
    section             VARCHAR(50)  NOT NULL,
    cdn_url             TEXT         NOT NULL,
    storage_key         TEXT         NOT NULL,
    hash                TEXT         NOT NULL,
    schema_version      VARCHAR(50)  NOT NULL,
    is_active           BOOLEAN      NOT NULL DEFAULT FALSE,
    is_ready            BOOLEAN      NOT NULL DEFAULT FALSE,
    created_by          BIGINT,
    created_at          TIMESTAMP(6) NOT NULL,
    updated_by          BIGINT,
    updated_at          TIMESTAMP(6) NOT NULL,
    deleted_by          BIGINT,
    deleted_at          TIMESTAMP(6) NOT NULL
);

CREATE INDEX idx_seat_layout_artifact_scope
    ON p_seat_layout_artifact (concert_id, concert_sequence_id, section);

CREATE INDEX idx_seat_layout_artifact_ready
    ON p_seat_layout_artifact (concert_id, concert_sequence_id, section, is_ready);

CREATE UNIQUE INDEX uk_seat_layout_artifact_active
    ON p_seat_layout_artifact (concert_id, concert_sequence_id, section)
    WHERE is_active = true AND deleted_at IS NULL;