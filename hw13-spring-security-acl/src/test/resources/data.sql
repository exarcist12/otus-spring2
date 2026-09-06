-- 1. Пользователи (явно указываем ID)
INSERT INTO users (id, username, password, role, enabled) VALUES
                                                              (1, 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_ADMIN', true),
                                                              (2, 'user1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER', true),
                                                              (3, 'user2', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'ROLE_USER', true);

-- 2. Авторы (user_id уникален! Author_3 получает NULL)
INSERT INTO authors (id, full_name, user_id) VALUES
                                                 (1, 'Author_1', 2),  -- Принадлежит user1
                                                 (2, 'Author_2', 3),  -- Принадлежит user2
                                                 (3, 'Author_3', NULL); -- Ни кому не принадлежит

-- 3. Жанры
INSERT INTO genres (id, name) VALUES
                                  (1, 'Genre_1'),
                                  (2, 'Genre_2'),
                                  (3, 'Genre_3');

-- 4. Книги
INSERT INTO books (id, title, author_id) VALUES
                                             (1, 'BookTitle_1', 1),
                                             (2, 'BookTitle_2', 2),
                                             (3, 'BookTitle_3', 3);

-- 5. Связи книг и жанров
INSERT INTO books_genres (book_id, genre_id) VALUES
                                                 (1, 1),
                                                 (2, 2),
                                                 (3, 3);

-- 6. Комментарии
INSERT INTO comments (id, text, book_id) VALUES
                                             (1, 'Отличная книга! Очень понравилась', 1),
                                             (2, 'Хороший сюжет, рекомендую', 1),
                                             (3, 'Неплохо, но могло быть лучше', 2),
                                             (4, 'Скучновато', 2),
                                             (5, 'Шедевр!', 3);