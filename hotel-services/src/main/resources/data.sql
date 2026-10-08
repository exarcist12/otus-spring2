INSERT INTO cities (name, country) VALUES
                                       ('Москва', 'Россия'),
                                       ('Санкт-Петербург', 'Россия'),
                                       ('Казань', 'Россия')
    ON CONFLICT DO NOTHING;

INSERT INTO amenities (name, icon) VALUES
                                       ('Wi-Fi', 'wifi'),
                                       ('Бассейн', 'pool'),
                                       ('Завтрак', 'breakfast'),
                                       ('Парковка', 'parking'),
                                       ('Спортзал', 'gym'),
                                       ('СПА', 'spa')
    ON CONFLICT DO NOTHING;

INSERT INTO users (username, password, email, role, enabled) VALUES
                                                                 ('admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'admin@hotel.com', 'ROLE_ADMIN', TRUE),
                                                                 ('user1', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'user1@hotel.com', 'ROLE_USER', TRUE),
                                                                 ('user2', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'user2@hotel.com', 'ROLE_USER', TRUE)
    ON CONFLICT (username) DO NOTHING;

INSERT INTO hotels (name, description, address, stars, city_id) VALUES
                                                                    ('Отель Москва Люкс', 'Роскошный отель в центре Москвы', 'ул. Тверская, 1', 5, 1),
                                                                    ('Отель Питер Стандарт', 'Уютный отель рядом с Невским проспектом', 'Невский пр., 10', 3, 2),
                                                                    ('Отель Казань Плаза', 'Современный отель в центре Казани', 'ул. Баумана, 5', 4, 3),
                                                                    ('Отель Москва Эконом', 'Бюджетный отель рядом с метро', 'ул. Арбат, 20', 2, 1)
    ON CONFLICT DO NOTHING;

INSERT INTO hotel_amenities (hotel_id, amenity_id) VALUES (1, 1), (1, 2), (1, 3), (1, 6)
    ON CONFLICT DO NOTHING;
INSERT INTO hotel_amenities (hotel_id, amenity_id) VALUES (2, 1), (2, 3)
    ON CONFLICT DO NOTHING;
INSERT INTO hotel_amenities (hotel_id, amenity_id) VALUES (3, 1), (3, 3), (3, 4), (3, 5)
    ON CONFLICT DO NOTHING;
INSERT INTO hotel_amenities (hotel_id, amenity_id) VALUES (4, 1)
    ON CONFLICT DO NOTHING;

INSERT INTO rooms (hotel_id, type, price_per_night, capacity, description) VALUES
                                                                               (1, 'LUX', 15000.00, 2, 'Люкс с видом на город'),
                                                                               (1, 'SUITE', 25000.00, 4, 'Президентский люкс'),
                                                                               (1, 'STANDARD', 7000.00, 2, 'Стандартный номер');

INSERT INTO rooms (hotel_id, type, price_per_night, capacity, description) VALUES
                                                                               (2, 'STANDARD', 4000.00, 2, 'Стандартный номер'),
                                                                               (2, 'COMFORT', 6000.00, 3, 'Комфортный номер');

INSERT INTO rooms (hotel_id, type, price_per_night, capacity, description) VALUES
                                                                               (3, 'COMFORT', 5500.00, 2, 'Комфорт'),
                                                                               (3, 'LUX', 12000.00, 2, 'Люкс');

INSERT INTO rooms (hotel_id, type, price_per_night, capacity, description) VALUES
    (4, 'STANDARD', 2500.00, 2, 'Стандарт');

INSERT INTO bookings (user_id, room_id, check_in, check_out, status, total_price, payment_id) VALUES
                                                                                                  (2, 1, '2026-10-06', '2026-10-15', 'PAID', 75000.00, 'pay_abc123'),
                                                                                                  (3, 5, '2026-11-01', '2026-11-05', 'PENDING', 24000.00, NULL)
    ON CONFLICT DO NOTHING;

INSERT INTO reviews (user_id, hotel_id, text, rating) VALUES
                                                          (2, 1, 'Отличный отель! Всё понравилось.', 5),
                                                          (2, 2, 'Неплохо, но шумно.', 3),
                                                          (3, 1, 'Дорого, но качественно.', 4)
    ON CONFLICT (user_id, hotel_id) DO NOTHING;