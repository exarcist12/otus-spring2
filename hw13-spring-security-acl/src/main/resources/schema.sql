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

-- Таблица пользователей (Spring Security)
create table users (
                       id bigserial primary key,
                       username varchar(255) not null unique,
                       password varchar(255) not null,
                       role varchar(50) not null,
                       enabled boolean default true
);

-- Добавляем связь автора с пользователем (один пользователь → один автор)
alter table authors add column user_id bigint unique references users(id);

-- Таблицы ACL (для авторизации на уровне доменных сущностей)
create table acl_sid (
                         id bigserial primary key,
                         principal boolean not null,
                         sid varchar(100) not null,
                         constraint unique_acl_sid unique(sid, principal)
);

create table acl_class (
                           id bigserial primary key,
                           class varchar(100) not null,
                           constraint unique_acl_class unique(class)
);

create table acl_object_identity (
                                     id bigserial primary key,
                                     object_id_class bigint not null,
                                     object_id_identity bigint not null,
                                     parent_object bigint,
                                     owner_sid bigint not null,
                                     entries_inheriting boolean not null,
                                     constraint unique_acl_object_identity unique(object_id_class, object_id_identity),
                                     constraint fk_acl_object_identity_class foreign key(object_id_class) references acl_class(id),
                                     constraint fk_acl_object_identity_parent foreign key(parent_object) references acl_object_identity(id),
                                     constraint fk_acl_object_identity_owner foreign key(owner_sid) references acl_sid(id)
);

create table acl_entry (
                           id bigserial primary key,
                           acl_object_identity bigint not null,
                           ace_order int not null,
                           sid bigint not null,
                           mask integer not null,
                           granting boolean not null,
                           audit_success boolean not null,
                           audit_failure boolean not null,
                           constraint unique_acl_entry unique(acl_object_identity, ace_order),
                           constraint fk_acl_entry_object foreign key(acl_object_identity) references acl_object_identity(id),
                           constraint fk_acl_entry_sid foreign key(sid) references acl_sid(id)
);
