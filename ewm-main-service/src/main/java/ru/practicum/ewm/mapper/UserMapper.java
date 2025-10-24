package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.dto.UserShortDto;
import ru.practicum.ewm.model.User;

public final class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(NewUserRequest dto) {
        return User.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .build();
    }

    public static UserDto toDto(User u) {
        return UserDto.builder()
                .id(u.getId())
                .name(u.getName())
                .email(u.getEmail())
                .build();
    }

    public static UserShortDto toShort(User u) {
        return UserShortDto.builder()
                .id(u.getId())
                .name(u.getName())
                .build();
    }
}
