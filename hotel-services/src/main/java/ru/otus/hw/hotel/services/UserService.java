package ru.otus.hw.hotel.services;

import ru.otus.hw.hotel.dto.RegisterUserRequest;
import ru.otus.hw.hotel.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(Long id);

    UserDto findByUsername(String username);

    UserDto register(RegisterUserRequest request);

    void deleteById(Long id);
}