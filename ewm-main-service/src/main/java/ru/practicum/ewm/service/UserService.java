package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repo.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class UserService {

    UserRepository userRepository;

    public UserDto create(NewUserRequest dto) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new ConflictException("Email уже используется: " + dto.getEmail());
        }
        User saved = userRepository.save(UserMapper.toEntity(dto));
        return UserMapper.toDto(saved);
    }

    public void delete(Long userId) {
        userRepository.delete(userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден: " + userId)));
    }

    public List<UserDto> getAll(List<Long> ids, Pageable pageable) {
        List<User> list = (ids == null || ids.isEmpty())
                ? userRepository.findAll(pageable).getContent()
                : userRepository.findAllById(ids);
        return list.stream().map(UserMapper::toDto).toList();
    }

    public User getOr404(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден: " + id));
    }
}
