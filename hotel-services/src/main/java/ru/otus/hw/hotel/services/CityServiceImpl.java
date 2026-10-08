package ru.otus.hw.hotel.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.hotel.dto.CityDto;
import ru.otus.hw.hotel.dto.CreateCityRequest;
import ru.otus.hw.hotel.exceptions.EntityNotFoundException;
import ru.otus.hw.hotel.mappers.CityMapper;
import ru.otus.hw.hotel.models.City;
import ru.otus.hw.hotel.repositories.CityRepository;
import ru.otus.hw.hotel.services.CityService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CityServiceImpl implements CityService {

    private final CityRepository cityRepository;
    private final CityMapper cityMapper;

    @Override
    @Transactional(readOnly = true)
    public List<CityDto> findAll() {
        return cityMapper.toDtoList(cityRepository.findAll());
    }

    @Override
    @Transactional(readOnly = true)
    public CityDto findById(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("City with id %d not found".formatted(id)));
        return cityMapper.toDto(city);
    }

    @Override
    public CityDto create(CreateCityRequest request) {
        City city = new City();
        city.setName(request.getName());
        city.setCountry(request.getCountry());
        City saved = cityRepository.save(city);
        return cityMapper.toDto(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (!cityRepository.existsById(id)) {
            throw new EntityNotFoundException("City with id %d not found".formatted(id));
        }
        cityRepository.deleteById(id);
    }
}