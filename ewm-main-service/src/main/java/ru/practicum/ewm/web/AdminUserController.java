package ru.practicum.ewm.web;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.NewUserRequest;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.service.UserService;
import ru.practicum.ewm.util.PageUtils;

@RestController
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping({"/admin/users", "/admin/users/"})
public class AdminUserController {

    UserService userService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody NewUserRequest dto) {
        return userService.create(dto);
    }

    @GetMapping
    public List<UserDto> list(@RequestParam(required = false) List<Long> ids,
                              @RequestParam(defaultValue = "0") Integer from,
                              @RequestParam(defaultValue = "10") Integer size) {
        return userService.getAll(ids, PageUtils.offsetPage(from, size, Sort.by("id").ascending()));
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
