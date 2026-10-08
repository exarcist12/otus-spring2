CREATE TABLE IF NOT EXISTS cities (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL UNIQUE,
    country VARCHAR(255) NOT NULL
    );

CREATE TABLE IF NOT EXISTS amenities (
                                         id BIGSERIAL PRIMARY KEY,
                                         name VARCHAR(255) NOT NULL UNIQUE,
    icon VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS hotels (
                                      id BIGSERIAL PRIMARY KEY,
                                      name VARCHAR(255) NOT NULL,
    description TEXT,
    address VARCHAR(500) NOT NULL,
    stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5),
    city_id BIGINT NOT NULL REFERENCES cities(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS hotel_amenities (
    hotel_id BIGINT NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    amenity_id BIGINT NOT NULL REFERENCES amenities(id) ON DELETE CASCADE,
    PRIMARY KEY (hotel_id, amenity_id)
    );

CREATE TABLE IF NOT EXISTS rooms (
                                     id BIGSERIAL PRIMARY KEY,
                                     hotel_id BIGINT NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    type VARCHAR(50) NOT NULL,
    price_per_night NUMERIC(10, 2) NOT NULL,
    capacity INT NOT NULL CHECK (capacity > 0),
    description TEXT
    );

CREATE TABLE IF NOT EXISTS users (
                                     id BIGSERIAL PRIMARY KEY,
                                     username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    role VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

CREATE TABLE IF NOT EXISTS bookings (
                                        id BIGSERIAL PRIMARY KEY,
                                        user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    room_id BIGINT NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    check_in DATE NOT NULL,
    check_out DATE NOT NULL,
    status VARCHAR(50) NOT NULL,
    total_price NUMERIC(10, 2) NOT NULL,
    payment_id VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CHECK (check_out > check_in)
    );

CREATE TABLE IF NOT EXISTS reviews (
                                       id BIGSERIAL PRIMARY KEY,
                                       user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    hotel_id BIGINT NOT NULL REFERENCES hotels(id) ON DELETE CASCADE,
    text TEXT NOT NULL,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, hotel_id)
    );