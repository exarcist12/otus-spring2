package ru.otus.hw.hotel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.hotel.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}