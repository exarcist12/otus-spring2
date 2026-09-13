-- Пользователи (пароль: password)
INSERT INTO users (username, password, role, enabled) VALUES ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_ADMIN', TRUE);
INSERT INTO users (username, password, role, enabled) VALUES ('user1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER', TRUE);
INSERT INTO users (username, password, role, enabled) VALUES ('user2', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER', TRUE);

-- Авторы с привязкой к пользователям
INSERT INTO authors (full_name, user_id) VALUES ('Author_1', 2);
INSERT INTO authors (full_name, user_id) VALUES ('Author_2', 3);
INSERT INTO authors (full_name, user_id) VALUES ('Author_3', 2);

-- Жанры
INSERT INTO genres (name) VALUES ('Genre_1');
INSERT INTO genres (name) VALUES ('Genre_2');
INSERT INTO genres (name) VALUES ('Genre_3');

-- Книги
INSERT INTO books (title, author_id) VALUES ('BookTitle_1', 1);
INSERT INTO books (title, author_id) VALUES ('BookTitle_2', 2);
INSERT INTO books (title, author_id) VALUES ('BookTitle_3', 3);

-- Связь книг с жанрами
INSERT INTO books_genres (book_id, genre_id) VALUES (1, 1);
INSERT INTO books_genres (book_id, genre_id) VALUES (2, 2);
INSERT INTO books_genres (book_id, genre_id) VALUES (3, 3);