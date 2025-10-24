package ru.practicum.ewm.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.service.EventService;
import ru.practicum.ewm.service.SubscriptionService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/users/{userId}/subscriptions")
public class PrivateSubscriptionController {

    SubscriptionService subscriptionService;
    EventService eventService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto subscribe(@PathVariable Long userId, @RequestParam @Positive Long targetId) {
        return subscriptionService.subscribe(userId, targetId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void unsubscribe(@PathVariable Long userId, @RequestParam @Positive Long targetId) {
        subscriptionService.unsubscribe(userId, targetId);
    }

    @GetMapping
    public List<UserDto> mySubscriptions(@PathVariable Long userId) {
        return subscriptionService.getSubscriptions(userId);
    }

    @GetMapping("/followers")
    public List<UserDto> myFollowers(@PathVariable Long userId) {
        return subscriptionService.getFollowers(userId);
    }

    @GetMapping("/feed")
    public List<EventShortDto> feed(@PathVariable Long userId,
                                    @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                    @RequestParam(defaultValue = "10") @Positive Integer size) {
        return eventService.getFeed(userId, from, size);
    }
}
