insert into authors(full_name)
values ('Author_1'), ('Author_2'), ('Author_3');

insert into genres(name)
values ('Genre_1'), ('Genre_2'), ('Genre_3');

insert into books(title, author_id)
values ('BookTitle_1', 1), ('BookTitle_2', 2), ('BookTitle_3', 3);

insert into books_genres(book_id, genre_id)
values (1, 1), (2, 2), (3, 3);

insert into comments(text, book_id)
values
    ('Отличная книга! Очень понравилась', 1),
    ('Хороший сюжет, рекомендую', 1),
    ('Неплохо, но могло быть лучше', 2),
    ('Скучновато', 2),
    ('Шедевр!', 3);


insert into users (username, password, role, enabled)
values
    ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_ADMIN', true),
    ('user1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER', true),
    ('user2', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER', true);

update authors set user_id = 2 where id = 1;
update authors set user_id = 3 where id = 2;