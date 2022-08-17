create table IF NOT EXISTS DIRECTORS
(
    ID   INTEGER auto_increment,
    NAME CHARACTER VARYING(100) not null
);

create unique index IF NOT EXISTS DIRECTORS_ID_UINDEX
    on DIRECTORS (ID);

alter table DIRECTORS
    add constraint DIRECTORS_PK
        primary key (ID);

create table IF NOT EXISTS GENRES
(
    ID   INTEGER               not null,
    NAME CHARACTER VARYING(30) not null
);

create unique index IF NOT EXISTS GENRES_ID_UINDEX
    on GENRES (ID);

create unique index IF NOT EXISTS GENRES_NAME_UINDEX
    on GENRES (NAME);

alter table GENRES
    add constraint GENRES_PK
        primary key (ID);

create table IF NOT EXISTS MPA
(
    ID   INTEGER              not null,
    NAME CHARACTER VARYING(5) not null
);

create unique index IF NOT EXISTS MPA_ID_UINDEX
    on MPA (ID);

create unique index IF NOT EXISTS MPA_NAME_UINDEX
    on MPA (NAME);

alter table MPA
    add constraint MPA_PK
        primary key (ID);

create table IF NOT EXISTS FILMS
(
    ID           INTEGER auto_increment,
    NAME         CHARACTER VARYING(150) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    DURATION     INTEGER default 100    not null,
    RELEASE_DATE DATE                   not null,
    MPA_ID       INTEGER                not null,
    constraint FK_MPA
        foreign key (MPA_ID) references MPA
);

create unique index IF NOT EXISTS FILMS_FILM_ID_UINDEX
    on FILMS (ID);

alter table FILMS
    add constraint FILMS_PK
        primary key (ID);

create table IF NOT EXISTS FILM_DIRECTOR
(
    FILM_ID     INTEGER not null,
    DIRECTOR_ID INTEGER not null,
    constraint FILM_DIRECTOR_PK_2
        primary key (FILM_ID, DIRECTOR_ID),
    constraint FILM_DIRECTOR_DIRECTORS_ID_FK
        foreign key (DIRECTOR_ID) references DIRECTORS
            on delete cascade,
    constraint FILM_DIRECTOR_FILMS_ID_FK
        foreign key (FILM_ID) references FILMS
            on delete cascade
);

create table IF NOT EXISTS FILM_GENRES
(
    GENRE_ID INTEGER not null,
    FILM_ID  INTEGER not null,
    constraint FILM_GENRES_PK
        primary key (GENRE_ID, FILM_ID),
    constraint FK_FILMS
        foreign key (FILM_ID) references FILMS
            on delete cascade,
    constraint FK_GENRES
        foreign key (GENRE_ID) references GENRES
            on delete cascade
);

create table IF NOT EXISTS USERS
(
    ID       INTEGER auto_increment,
    LOGIN    CHARACTER VARYING(50)  not null,
    NAME     CHARACTER VARYING(50)  not null,
    EMAIL    CHARACTER VARYING(100) not null,
    BIRTHDAY DATE                   not null,
    constraint USERS_PK
        primary key (ID)
);

create table IF NOT EXISTS EVENTS
(
    ID         INTEGER auto_increment,
    EVENT_TIME TIMESTAMP             not null,
    USER_ID    INTEGER               not null,
    EVENT_TYPE CHARACTER VARYING(10) not null,
    OPERATION  CHARACTER VARYING(10) not null,
    ENTITY_ID  INTEGER               not null,
    constraint EVENTS_USERS_ID_FK
        foreign key (USER_ID) references USERS
            on delete cascade
);

create unique index IF NOT EXISTS EVENTS_ID_UINDEX
    on EVENTS (ID);

alter table EVENTS
    add constraint EVENTS_PK
        primary key (ID);

create table IF NOT EXISTS FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    constraint FRIENDS_PK
        primary key (USER_ID, FRIEND_ID),
    constraint FK_FRIENDS_USERS
        foreign key (USER_ID) references USERS
            on delete cascade,
    constraint FK_FRIENDS_USERS_2
        foreign key (FRIEND_ID) references USERS
            on delete cascade
);

create table IF NOT EXISTS LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint LIKES_KEY
        primary key (FILM_ID, USER_ID),
    constraint FK_LIKES_FILMS
        foreign key (FILM_ID) references FILMS
            on delete cascade,
    constraint FK_USERS
        foreign key (USER_ID) references USERS
            on delete cascade
);

create table IF NOT EXISTS REVIEWS
(
    ID           INTEGER                not null,
    CONTENT      CHARACTER VARYING(500) not null,
    "isPositive" BOOLEAN                not null,
    USER_ID      INTEGER                not null,
    FILM_ID      INTEGER                not null,
    constraint REVIEWS_PK
        primary key (ID),
    constraint REVIEWS_FILMS_ID_FK
        foreign key (FILM_ID) references FILMS
            on delete cascade,
    constraint REVIEWS_USERS_ID_FK
        foreign key (USER_ID) references USERS
            on delete cascade
);

create unique index IF NOT EXISTS REVIEWS_ID_UINDEX
    on REVIEWS (ID);

create table IF NOT EXISTS REVIEW_USEFUL
(
    REVIEW_ID INTEGER not null,
    USER_ID   INTEGER not null,
    IS_LIKE   BOOLEAN not null,
    constraint REVIEW_USEFUL_PK
        primary key (REVIEW_ID, USER_ID),
    constraint REVIEW_USEFUL_REVIEWS_ID_FK
        foreign key (REVIEW_ID) references REVIEWS
            on delete cascade,
    constraint REVIEW_USEFUL_USERS_ID_FK
        foreign key (USER_ID) references USERS
            on delete cascade
);

create unique index IF NOT EXISTS USERS_EMAIL_UINDEX
    on USERS (EMAIL);

create unique index IF NOT EXISTS USERS_ID_UINDEX
    on USERS (ID);

create unique index IF NOT EXISTS USERS_LOGIN_UINDEX
    on USERS (LOGIN);