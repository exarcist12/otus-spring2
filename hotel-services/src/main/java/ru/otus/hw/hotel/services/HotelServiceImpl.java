package ru.otus.hw.hotel.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.hotel.dto.CreateHotelRequest;
import ru.otus.hw.hotel.dto.HotelDto;
import ru.otus.hw.hotel.dto.HotelShortDto;
import ru.otus.hw.hotel.dto.UpdateHotelRequest;
import ru.otus.hw.hotel.exceptions.EntityNotFoundException;
import ru.otus.hw.hotel.mappers.HotelMapper;
import ru.otus.hw.hotel.models.Amenity;
import ru.otus.hw.hotel.models.City;
import ru.otus.hw.hotel.models.Hotel;
import ru.otus.hw.hotel.repositories.AmenityRepository;
import ru.otus.hw.hotel.repositories.CityRepository;
import ru.otus.hw.hotel.repositories.HotelRepository;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class HotelServiceImpl implements HotelService {

    private final HotelRepository hotelRepository;
    private final CityRepository cityRepository;
    private final AmenityRepository amenityRepository;
    private final HotelMapper hotelMapper;

    @Override
    @Transactional(readOnly = true)
    public List<HotelShortDto> findAll() {
        return hotelMapper.toShortDtoList(hotelRepository.findAllWithDetails());
    }

    @Override
    @Transactional(readOnly = true)
    public List<HotelShortDto> findByCityId(Long cityId) {
        return hotelMapper.toShortDtoList(hotelRepository.findByCityIdWithDetails(cityId));
    }

    @Override
    @Transactional(readOnly = true)
    public HotelDto findById(Long id) {
        Hotel hotel = hotelRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel with id %d not found".formatted(id)));
        return hotelMapper.toDto(hotel);
    }

    @Override
    public HotelDto create(CreateHotelRequest request) {
        Hotel hotel = new Hotel();
        applyRequest(hotel, request);
        Hotel saved = hotelRepository.save(hotel);
        return hotelMapper.toDto(saved);
    }

    @Override
    public HotelDto update(Long id, UpdateHotelRequest request) {
        Hotel hotel = hotelRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel with id %d not found".formatted(id)));
        applyRequest(hotel, request);
        Hotel saved = hotelRepository.save(hotel);
        return hotelMapper.toDto(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (!hotelRepository.existsById(id)) {
            throw new EntityNotFoundException("Hotel with id %d not found".formatted(id));
        }
        hotelRepository.deleteById(id);
    }

    private void applyRequest(Hotel hotel, Object request) {
        String name;
        String description;
        String address;
        Integer stars;
        Long cityId;
        Set<Long> amenityIds;

        if (request instanceof CreateHotelRequest r) {
            name = r.getName();
            description = r.getDescription();
            address = r.getAddress();
            stars = r.getStars();
            cityId = r.getCityId();
            amenityIds = r.getAmenityIds();
        } else if (request instanceof UpdateHotelRequest r) {
            name = r.getName();
            description = r.getDescription();
            address = r.getAddress();
            stars = r.getStars();
            cityId = r.getCityId();
            amenityIds = r.getAmenityIds();
        } else {
            throw new IllegalArgumentException("Unknown request type");
        }

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new EntityNotFoundException("City with id %d not found".formatted(cityId)));

        hotel.setName(name);
        hotel.setDescription(description);
        hotel.setAddress(address);
        hotel.setStars(stars);
        hotel.setCity(city);

        if (amenityIds != null && !amenityIds.isEmpty()) {
            List<Amenity> amenities = amenityRepository.findAllByIdIn(amenityIds);
            if (amenities.size() != amenityIds.size()) {
                throw new EntityNotFoundException("Some amenities not found");
            }
            hotel.setAmenities(new HashSet<>(amenities));
        } else {
            hotel.setAmenities(new HashSet<>());
        }
    }
}