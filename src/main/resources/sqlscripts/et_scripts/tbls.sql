create table venue
(
    id              uuid not null
        constraint venues_pk
            primary key,
    tribunal_office varchar,
    code            varchar,
    label           varchar
);

create table room
(
    id       uuid not null
        constraint rooms_pk
            primary key,
    code     varchar,
    label    varchar,
    venue_id uuid
        constraint room_venue_id_fk
            references venue
);


create table court_worker
(
    id              uuid not null
        constraint lookups_pk
            primary key,
    lookup_id       varchar,
    tribunal_office varchar,
    code            varchar,
    label           varchar
);


