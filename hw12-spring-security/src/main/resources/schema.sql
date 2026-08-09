create table authors (
                         id bigserial,
                         full_name varchar(255),
                         primary key (id)
);

create table genres (
                        id bigserial,
                        name varchar(255),
                        primary key (id)
);

create table books (
                       id bigserial,
                       title varchar(255),
                       author_id bigint references authors (id) on delete cascade,
                       primary key (id)
);

create table books_genres (
                              book_id bigint references books(id) on delete cascade,
                              genre_id bigint references genres(id) on delete cascade,
                              primary key (book_id, genre_id)
);

create table comments (
                          id bigserial primary key,
                          text varchar(1000) not null,
                          book_id bigint references books(id) on delete cascade
);

create table users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(255) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       role VARCHAR(50) NOT NULL,
                       enabled BOOLEAN DEFAULT TRUE
);