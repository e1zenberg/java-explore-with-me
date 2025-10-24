package ru.practicum.ewm.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.model.Subscription;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    boolean existsByFollowerIdAndTargetId(Long followerId, Long targetId);

    Optional<Subscription> findByFollowerIdAndTargetId(Long followerId, Long targetId);

    List<Subscription> findAllByFollowerId(Long followerId);

    List<Subscription> findAllByTargetId(Long targetId);
}
