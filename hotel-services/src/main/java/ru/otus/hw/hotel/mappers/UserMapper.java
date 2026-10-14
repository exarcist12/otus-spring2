package ru.otus.hw.hotel.mappers;

import org.springframework.stereotype.Component;
import ru.otus.hw.hotel.dto.UserDto;
import ru.otus.hw.hotel.models.User;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );
    }
}