package ru.otus.hw.hotel.services;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.otus.hw.hotel.dto.RegisterUserRequest;
import ru.otus.hw.hotel.dto.UserDto;
import ru.otus.hw.hotel.enums.Role;
import ru.otus.hw.hotel.exceptions.EntityNotFoundException;
import ru.otus.hw.hotel.mappers.UserMapper;
import ru.otus.hw.hotel.models.User;
import ru.otus.hw.hotel.repositories.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id %d not found".formatted(id)));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFoundException("User with username %s not found".formatted(username)));
        return userMapper.toDto(user);
    }

    @Override
    public UserDto register(RegisterUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username %s is already taken".formatted(request.getUsername()));
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email %s is already registered".formatted(request.getEmail()));
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(Role.ROLE_USER);
        user.setEnabled(true);

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public void deleteById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("User with id %d not found".formatted(id));
        }
        userRepository.deleteById(id);
    }
}