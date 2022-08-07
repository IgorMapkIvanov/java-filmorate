create table IF NOT EXISTS GENRES
(
    ID   INTEGER auto_increment,
    NAME CHARACTER VARYING(30) not null
);

create unique index IF NOT EXISTS GENRES_ID_UINDEX
    on GENRES (ID);

create unique index IF NOT EXISTS GENRES_NAME_UINDEX
    on GENRES (NAME);

alter table GENRES
    add constraint IF NOT EXISTS GENRES_PK
        primary key (ID);

create table IF NOT EXISTS MPA
(
    ID          INTEGER                not null,
    CODE        CHARACTER VARYING(5)   not null,
    DESCRIPTION CHARACTER VARYING(100) not null
);

create unique index IF NOT EXISTS MPA_ID_UINDEX
    on MPA (ID);

create unique index IF NOT EXISTS MPA_NAME_UINDEX
    on MPA (CODE);

alter table MPA
    add constraint IF NOT EXISTS MPA_PK
        primary key (ID);

create table IF NOT EXISTS FILMS
(
    ID           INTEGER auto_increment,
    NAME         CHARACTER VARYING(150) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    DURATION     INTEGER default 100    not null,
    RELEASE_DATE DATE                   not null,
    MPA_ID       INTEGER                not null,
    constraint IF NOT EXISTS FK_MPA
        foreign key (MPA_ID) references MPA
);

create unique index IF NOT EXISTS FILMS_FILM_ID_UINDEX
    on FILMS (ID);

alter table FILMS
    add constraint IF NOT EXISTS FILMS_PK
        primary key (ID);

create table IF NOT EXISTS FILM_GENRES
(
    GENRE_ID INTEGER not null,
    FILM_ID  INTEGER not null,
    constraint IF NOT EXISTS FILM_GENRES_PK
        primary key (GENRE_ID, FILM_ID),
    constraint IF NOT EXISTS FK_FILMS
        foreign key (FILM_ID) references FILMS,
    constraint IF NOT EXISTS FK_GENRES
        foreign key (GENRE_ID) references GENRES
);

create table IF NOT EXISTS USERS
(
    ID       INTEGER auto_increment,
    LOGIN    CHARACTER VARYING(50)  not null,
    NAME     CHARACTER VARYING(50)  not null,
    EMAIL    CHARACTER VARYING(100) not null,
    BIRTHDAY DATE                   not null,
    constraint IF NOT EXISTS USERS_PK
        primary key (ID)
);

create table IF NOT EXISTS FRIENDS
(
    USER_ID   INTEGER not null,
    FRIEND_ID INTEGER not null,
    STATUS    BOOLEAN not null,
    constraint IF NOT EXISTS FRIENDS_PK
        primary key (USER_ID, FRIEND_ID),
    constraint IF NOT EXISTS FK_FRIENDS_USERS
        foreign key (USER_ID) references USERS,
    constraint IF NOT EXISTS FK_FRIENDS_USERS_2
        foreign key (FRIEND_ID) references USERS
);

create table IF NOT EXISTS LIKES
(
    FILM_ID INTEGER not null,
    USER_ID INTEGER not null,
    constraint IF NOT EXISTS LIKES_KEY
        primary key (FILM_ID, USER_ID),
    constraint IF NOT EXISTS FK_LIKES_FILMS
        foreign key (FILM_ID) references FILMS,
    constraint IF NOT EXISTS FK_USERS
        foreign key (USER_ID) references USERS
);

create unique index IF NOT EXISTS USERS_EMAIL_UINDEX
    on USERS (EMAIL);

create unique index IF NOT EXISTS USERS_ID_UINDEX
    on USERS (ID);

create unique index IF NOT EXISTS USERS_LOGIN_UINDEX
    on USERS (LOGIN);