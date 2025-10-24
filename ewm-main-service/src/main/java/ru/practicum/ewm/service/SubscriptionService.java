package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.UserDto;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.UserMapper;
import ru.practicum.ewm.model.Subscription;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repo.SubscriptionRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class SubscriptionService {

    SubscriptionRepository subscriptionRepository;
    UserService userService;

    @Transactional
    public UserDto subscribe(Long followerId, Long targetId) {
        if (followerId.equals(targetId)) {
            throw new ConflictException("Нельзя подписаться на самого себя");
        }
        User follower = userService.getOr404(followerId);
        User target = userService.getOr404(targetId);
        if (subscriptionRepository.existsByFollowerIdAndTargetId(followerId, targetId)) {
            throw new ConflictException("Подписка уже существует");
        }
        Subscription saved = subscriptionRepository.save(Subscription.builder()
                .follower(follower)
                .target(target)
                .createdOn(LocalDateTime.now())
                .build());
        return UserMapper.toDto(saved.getTarget());
    }

    @Transactional
    public void unsubscribe(Long followerId, Long targetId) {
        Subscription subscription = subscriptionRepository.findByFollowerIdAndTargetId(followerId, targetId)
                .orElseThrow(() -> new NotFoundException("Подписка не найдена"));
        subscriptionRepository.delete(subscription);
    }

    public List<UserDto> getSubscriptions(Long followerId) {
        userService.getOr404(followerId);
        return subscriptionRepository.findAllByFollowerId(followerId).stream()
                .map(Subscription::getTarget)
                .map(UserMapper::toDto)
                .toList();
    }

    public List<UserDto> getFollowers(Long targetId) {
        userService.getOr404(targetId);
        return subscriptionRepository.findAllByTargetId(targetId).stream()
                .map(Subscription::getFollower)
                .map(UserMapper::toDto)
                .toList();
    }
}
