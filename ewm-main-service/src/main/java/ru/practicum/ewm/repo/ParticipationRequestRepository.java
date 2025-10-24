package ru.practicum.ewm.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.RequestStatus;

import java.util.List;

public interface ParticipationRequestRepository extends JpaRepository<ParticipationRequest, Long> {

    long countByEventIdAndStatus(Long eventId, RequestStatus status);

    boolean existsByEventIdAndRequesterId(Long eventId, Long requesterId);

    List<ParticipationRequest> findAllByRequesterId(Long requesterId);

    List<ParticipationRequest> findAllByEventId(Long eventId);

    @Query("select r from ParticipationRequest r where r.id in :ids and r.event.id = :eventId")
    List<ParticipationRequest> findAllByIdsAndEventId(List<Long> ids, Long eventId);
}
