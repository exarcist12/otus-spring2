INSERT INTO authors (full_name) VALUES ('Author 1'), ('Author 2'), ('Author 3');
INSERT INTO genres (name) VALUES ('Genre 1'), ('Genre 2'), ('Genre 3'), ('Genre 4'), ('Genre 5'), ('Genre 6');
INSERT INTO books (title, author_id) VALUES ('Book 1', 1), ('Book 2', 2), ('Book 3', 3);
INSERT INTO books_genres (book_id, genre_id) VALUES (1,1), (1,2), (2,3), (2,4), (3,5), (3,6);

insert into comments(text, book_id)
values
    ('Отличная книга! Очень понравилась', 1),
    ('Хороший сюжет, рекомендую', 1),
    ('Неплохо, но могло быть лучше', 2),
    ('Скучновато', 2),
    ('Шедевр!', 3);