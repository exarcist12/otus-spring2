create table authors (
                         id bigserial primary key,
                         full_name varchar(255)
);

create table genres (
                        id bigserial primary key,
                        name varchar(255)
);

create table books (
                       id bigserial primary key,
                       title varchar(255),
                       author_id bigint references authors (id) on delete cascade
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
                       id bigserial primary key,
                       username varchar(255) not null unique,
                       password varchar(255) not null,
                       role varchar(50) not null,
                       enabled boolean default true
);


alter table authors add column user_id bigint unique references users(id);


CREATE TABLE acl_sid (
                         id BIGSERIAL PRIMARY KEY,
                         principal BOOLEAN NOT NULL,
                         sid VARCHAR(100) NOT NULL,
                         CONSTRAINT unique_acl_sid UNIQUE (sid, principal)
);

CREATE TABLE acl_class (
                           id BIGSERIAL PRIMARY KEY,
                           class VARCHAR(100) NOT NULL,
                           CONSTRAINT unique_acl_class UNIQUE (class)
);

CREATE TABLE acl_object_identity (
                                     id BIGSERIAL PRIMARY KEY,
                                     object_id_class BIGINT NOT NULL,
                                     object_id_identity VARCHAR(36) NOT NULL,
                                     parent_object BIGINT,
                                     owner_sid BIGINT NOT NULL,
                                     entries_inheriting BOOLEAN NOT NULL,
                                     CONSTRAINT unique_acl_object_identity UNIQUE (object_id_class, object_id_identity),
                                     CONSTRAINT fk_acl_object_identity_class FOREIGN KEY (object_id_class) REFERENCES acl_class(id),
                                     CONSTRAINT fk_acl_object_identity_parent FOREIGN KEY (parent_object) REFERENCES acl_object_identity(id),
                                     CONSTRAINT fk_acl_object_identity_owner FOREIGN KEY (owner_sid) REFERENCES acl_sid(id)
);

CREATE TABLE acl_entry (
                           id BIGSERIAL PRIMARY KEY,
                           acl_object_identity BIGINT NOT NULL,
                           ace_order INTEGER NOT NULL,
                           sid BIGINT NOT NULL,
                           mask INTEGER NOT NULL,
                           granting BOOLEAN NOT NULL,
                           audit_success BOOLEAN NOT NULL,
                           audit_failure BOOLEAN NOT NULL,
                           CONSTRAINT unique_acl_entry UNIQUE (acl_object_identity, ace_order),
                           CONSTRAINT fk_acl_entry_object FOREIGN KEY (acl_object_identity) REFERENCES acl_object_identity(id),
                           CONSTRAINT fk_acl_entry_sid FOREIGN KEY (sid) REFERENCES acl_sid(id)
);