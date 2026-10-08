package ru.otus.hw.hotel.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.otus.hw.hotel.dto.LoginRequest;
import ru.otus.hw.hotel.dto.LoginResponse;
import ru.otus.hw.hotel.dto.RegisterUserRequest;
import ru.otus.hw.hotel.dto.UserDto;
import ru.otus.hw.hotel.security.JwtTokenProvider;
import ru.otus.hw.hotel.services.UserService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody RegisterUserRequest request) {
        return userService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        // 1. Spring Security проверяет логин/пароль
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 2. Генерируем JWT
        String token = jwtTokenProvider.generateToken(authentication);

        // 3. Возвращаем токен + информацию о пользователе
        UserDto user = userService.findByUsername(request.getUsername());
        return new LoginResponse(token, user);
    }

    @GetMapping("/me")
    public UserDto me(Authentication authentication) {
        return userService.findByUsername(authentication.getName());
    }
}