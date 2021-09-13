create table "Venues"
(
    id              integer not null
        constraint venues_pk
            primary key,
    tribunal_office varchar,
    code            varchar,
    label           varchar
);


create table "Rooms"
(
    id              integer not null
        constraint rooms_pk
            primary key,
    tribunal_office varchar,
    code            varchar,
    label           integer
);


create table "LookUps"
(
    id              integer not null
        constraint lookups_pk
            primary key,
    lookup_id       varchar,
    tribunal_office varchar,
    code            varchar,
    label           varchar
);


