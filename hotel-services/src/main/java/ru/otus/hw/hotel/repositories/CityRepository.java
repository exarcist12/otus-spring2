package ru.otus.hw.hotel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.hotel.models.City;

import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {

    Optional<City> findByName(String name);
}