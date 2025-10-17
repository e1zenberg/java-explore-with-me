package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.dto.ParticipationRequestDto;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.RequestMapper;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.ParticipationRequest;
import ru.practicum.ewm.model.RequestStatus;
import ru.practicum.ewm.model.User;
import ru.practicum.ewm.repo.ParticipationRequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class RequestService {

    ParticipationRequestRepository requestRepository;
    EventService eventService;
    UserService userService;

    @Transactional
    public ParticipationRequestDto createRequest(Long userId, Long eventId) {
        User requester = userService.getOr404(userId);
        Event event = eventService.getOr404(eventId);

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Инициатор не может подать заявку на собственное событие");
        }
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConflictException("Заявка доступна только для опубликованных событий");
        }
        if (requestRepository.existsByEventIdAndRequesterId(eventId, userId)) {
            throw new ConflictException("Заявка уже существует");
        }
        long confirmed = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        if (event.getParticipantLimit() != 0 && confirmed >= event.getParticipantLimit()) {
            throw new ConflictException("Лимит участников достигнут");
        }

        boolean autoConfirm = Boolean.FALSE.equals(event.getRequestModeration()) || event.getParticipantLimit() == 0;
        RequestStatus status = autoConfirm ? RequestStatus.CONFIRMED : RequestStatus.PENDING;

        ParticipationRequest r = ParticipationRequest.builder()
                .created(LocalDateTime.now())
                .event(event)
                .requester(requester)
                .status(status)
                .build();
        return RequestMapper.toDto(requestRepository.save(r));
    }

    public List<ParticipationRequestDto> getUserRequests(Long userId) {
        return requestRepository.findAllByRequesterId(userId).stream().map(RequestMapper::toDto).toList();
    }

    @Transactional
    public ParticipationRequestDto cancel(Long userId, Long requestId) {
        ParticipationRequest r = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Заявка не найдена: " + requestId));
        if (!r.getRequester().getId().equals(userId)) {
            throw new NotFoundException("Заявка не найдена для пользователя: " + requestId);
        }
        r.setStatus(RequestStatus.CANCELED);
        return RequestMapper.toDto(requestRepository.save(r));
    }

    public List<ParticipationRequestDto> getEventRequests(Long userId, Long eventId) {
        Event e = eventService.getOr404(eventId);
        if (!e.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не найдено для пользователя");
        }
        return requestRepository.findAllByEventId(eventId).stream().map(RequestMapper::toDto).toList();
    }

    @Transactional
    public EventRequestStatusUpdateResult updateStatuses(Long userId, Long eventId, EventRequestStatusUpdateRequest body) {
        Event e = eventService.getOr404(eventId);
        if (!e.getInitiator().getId().equals(userId)) {
            throw new NotFoundException("Событие не найдено для пользователя");
        }

        if ("CONFIRMED".equalsIgnoreCase(body.getStatus())) {
            long confirmedCount = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
            if (e.getParticipantLimit() != 0 && confirmedCount >= e.getParticipantLimit()) {
                throw new ConflictException("Лимит участников достигнут");
            }
        }

        List<ParticipationRequest> requests = requestRepository.findAllByIdsAndEventId(body.getRequestIds(), eventId);

        List<ParticipationRequest> confirmed = new ArrayList<>();
        List<ParticipationRequest> rejected = new ArrayList<>();

        for (ParticipationRequest r : requests) {
            if (r.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Обновлять можно только заявки в статусе PENDING");
            }
            if ("CONFIRMED".equalsIgnoreCase(body.getStatus())) {
                long count = requestRepository.countByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
                if (e.getParticipantLimit() != 0 && count >= e.getParticipantLimit()) {
                    r.setStatus(RequestStatus.REJECTED);
                    rejected.add(r);
                } else {
                    r.setStatus(RequestStatus.CONFIRMED);
                    confirmed.add(r);
                }
            } else if ("REJECTED".equalsIgnoreCase(body.getStatus())) {
                r.setStatus(RequestStatus.REJECTED);
                rejected.add(r);
            } else {
                throw new ConflictException("Неизвестный статус: " + body.getStatus());
            }
        }

        requestRepository.saveAll(requests);

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmed.stream().map(RequestMapper::toDto).toList())
                .rejectedRequests(rejected.stream().map(RequestMapper::toDto).toList())
                .build();
    }
}
