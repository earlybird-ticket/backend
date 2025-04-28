create table public.p_outbox
(
    id             bigint  not null primary key,
    aggregate_id   uuid,
    aggregate_type varchar(255),
    created_at     timestamp(6),
    event_type     varchar(255),
    org_id         bigint,
    payload        text,
    retry_count    integer not null,
    sent_at        timestamp(6),
    success        boolean not null
);

alter table public.p_outbox
    owner to postgres;

create sequence p_outbox_seq
    increment by 100;

alter sequence p_outbox_seq owner to postgres;