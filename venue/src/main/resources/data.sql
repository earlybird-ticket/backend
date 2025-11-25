INSERT INTO p_venue (
    id, name, location, area, total_number_of_seats,
    created_at, created_by,
    updated_at, updated_by,
    deleted_at, deleted_by
)
VALUES (
           '33333333-3333-3333-3333-333333333333',
           '서울 콘서트홀',
           '서울특별시 송파구',
           'A구역',
           13000,
           NOW(), 1,
           NOW(), 1,
           NULL, NULL
       );

INSERT INTO p_hall (
    id, name, floor, venue_id,
    created_at, created_by,
    updated_at, updated_by,
    deleted_at, deleted_by
)
VALUES (
           '44444444-4444-4444-4444-444444444444',
           '1층 메인홀',
           1,
           '33333333-3333-3333-3333-333333333333',
           NOW(), 1,
           NOW(), 1,
           NULL, NULL
       );

INSERT INTO p_seat (
    id, venue_id, hall_id, section, row, col, floor,
    created_at, created_by, updated_at, updated_by, deleted_at, deleted_by
)
SELECT
    gen_random_uuid() AS id,
    '33333333-3333-3333-3333-333333333333' AS venue_id,
    '44444444-4444-4444-4444-444444444444' AS hall_id,
    section_tbl.section,
    r AS row,
    c AS col,
    1 AS floor,
    NOW(), 1, NOW(), 1, NULL, NULL
FROM generate_series(1,50) AS r
    CROSS JOIN generate_series(1,10) AS c
    CROSS JOIN (
    SELECT 'A' AS section UNION ALL SELECT 'B' UNION ALL SELECT 'C'
    UNION ALL SELECT 'D' UNION ALL SELECT 'E' UNION ALL SELECT 'F'
    UNION ALL SELECT 'G' UNION ALL SELECT 'H' UNION ALL SELECT 'I'
    UNION ALL SELECT 'J' UNION ALL SELECT 'K' UNION ALL SELECT 'L'
    UNION ALL SELECT 'M' UNION ALL SELECT 'N' UNION ALL SELECT 'O'
    UNION ALL SELECT 'P' UNION ALL SELECT 'Q' UNION ALL SELECT 'R'
    UNION ALL SELECT 'S' UNION ALL SELECT 'T' UNION ALL SELECT 'U'
    UNION ALL SELECT 'V' UNION ALL SELECT 'W' UNION ALL SELECT 'X'
    UNION ALL SELECT 'Y' UNION ALL SELECT 'Z'
    ) section_tbl;

INSERT INTO p_seat_instance (
    id, seat_id, venue_id, hall_id, concert_id, concert_sequence_id,
    grade, status, price,
    created_at, created_by, updated_at, updated_by, deleted_at, deleted_by
)
SELECT
    gen_random_uuid(),
    s.id,
    '33333333-3333-3333-3333-333333333333',
    '44444444-4444-4444-4444-444444444444',
    '11111111-1111-1111-1111-111111111111',
    '52aab2c6-82e4-4c10-b983-e26bdb8550de',
    'R',
    'FREE',
    100000,
    NOW(), 1, NOW(), 1, NULL, NULL
FROM p_seat s
WHERE s.venue_id = '33333333-3333-3333-3333-333333333333'
  AND s.hall_id  = '44444444-4444-4444-4444-444444444444'
  AND s.deleted_at IS NULL;

INSERT INTO p_seat_instance (
    id, seat_id, venue_id, hall_id, concert_id, concert_sequence_id,
    grade, status, price,
    created_at, created_by, updated_at, updated_by, deleted_at, deleted_by
)
SELECT
    gen_random_uuid(),
    s.id,
    '33333333-3333-3333-3333-333333333333',
    '44444444-4444-4444-4444-444444444444',
    '11111111-1111-1111-1111-111111111111',
    '2d6d2381-a295-46fa-b798-a891f523c726',
    'R',
    'FREE',
    100000,
    NOW(), 1, NOW(), 1, NULL, NULL
FROM p_seat s
WHERE s.venue_id = '33333333-3333-3333-3333-333333333333'
  AND s.hall_id  = '44444444-4444-4444-4444-444444444444'
  AND s.deleted_at IS NULL;

truncate p_seat_instance;