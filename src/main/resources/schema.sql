create table FILMS
(
    FILM_ID      INTEGER auto_increment,
    NAME         CHARACTER VARYING(150) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    DURATION     INTEGER default 100    not null,
    RELEASE_DATE DATE                   not null
);

create unique index FILMS_FILM_ID_UINDEX
    on FILMS (id);

alter table FILMS
    add constraint FILMS_PK
        primary key (id);

create table GENRES
(
    ID   INTEGER auto_increment,
    NAME CHARACTER VARYING(30) not null
);

create unique index GENRES_ID_UINDEX
    on GENRES (ID);

create unique index GENRES_NAME_UINDEX
    on GENRES (NAME);

alter table GENRES
    add constraint GENRES_PK
        primary key (ID);

create table FILM_GENRES
(
    GENRE_ID INTEGER not null,
    FILM_ID  INTEGER not null,
    constraint KEY_NAME
        primary key (GENRE_ID, FILM_ID),
    constraint FK_FILMS
        foreign key (FILM_ID) references FILMS,
    constraint FK_GENRES
        foreign key (GENRE_ID) references GENRES
);

comment on table FILM_GENRES is 'film genres';

comment on column FILM_GENRES.GENRE_ID is 'genre_id';

create table LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint LIKES_KEY
        primary key (FILM_ID, USER_ID),
    constraint FK_LIKES_FILMS
        foreign key (FILM_ID) references FILMS
);

comment on table LIKES is 'likes';

