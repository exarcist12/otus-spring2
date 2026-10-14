package ru.otus.hw.hotel.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.otus.hw.hotel.models.Amenity;

import java.util.List;
import java.util.Set;

public interface AmenityRepository extends JpaRepository<Amenity, Long> {

    List<Amenity> findAllByIdIn(Set<Long> ids);

    List<Amenity> findAllByNameIn(Set<String> names);
}