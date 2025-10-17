package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.EventFullDto;
import ru.practicum.ewm.dto.EventShortDto;
import ru.practicum.ewm.dto.Location;
import ru.practicum.ewm.dto.NewEventDto;
import ru.practicum.ewm.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.dto.UpdateEventUserRequest;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventLocation;
import ru.practicum.ewm.model.EventState;
import ru.practicum.ewm.model.User;

public final class EventMapper {

    private EventMapper() {
    }

    public static Event toEntity(NewEventDto dto, User initiator, Category category) {
        return Event.builder()
                .annotation(dto.getAnnotation())
                .category(category)
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(dto.getLocation() == null ? null
                        : EventLocation.builder()
                        .lat(dto.getLocation().getLat())
                        .lon(dto.getLocation().getLon())
                        .build())
                .paid(Boolean.TRUE.equals(dto.getPaid()))
                .participantLimit(dto.getParticipantLimit() == null ? 0 : dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration() == null ? true : dto.getRequestModeration())
                .title(dto.getTitle())
                .initiator(initiator)
                .state(EventState.PENDING)
                .build();
    }

    public static void applyUserUpdate(Event e, UpdateEventUserRequest dto, Category category) {
        if (dto.getAnnotation() != null) {
            e.setAnnotation(dto.getAnnotation());
        }
        if (dto.getCategory() != null && category != null) {
            e.setCategory(category);
        }
        if (dto.getDescription() != null) {
            e.setDescription(dto.getDescription());
        }
        if (dto.getEventDate() != null) {
            e.setEventDate(dto.getEventDate());
        }
        if (dto.getLocation() != null) {
            e.setLocation(EventLocation.builder()
                    .lat(dto.getLocation().getLat())
                    .lon(dto.getLocation().getLon())
                    .build());
        }
        if (dto.getPaid() != null) {
            e.setPaid(dto.getPaid());
        }
        if (dto.getParticipantLimit() != null) {
            e.setParticipantLimit(dto.getParticipantLimit());
        }
        if (dto.getRequestModeration() != null) {
            e.setRequestModeration(dto.getRequestModeration());
        }
        if (dto.getTitle() != null) {
            e.setTitle(dto.getTitle());
        }
    }

    public static void applyAdminUpdate(Event e, UpdateEventAdminRequest dto, Category category) {
        applyUserUpdate(e, toUserReq(dto), category);
    }

    private static UpdateEventUserRequest toUserReq(UpdateEventAdminRequest dto) {
        return UpdateEventUserRequest.builder()
                .annotation(dto.getAnnotation())
                .category(dto.getCategory())
                .description(dto.getDescription())
                .eventDate(dto.getEventDate())
                .location(dto.getLocation())
                .paid(dto.getPaid())
                .participantLimit(dto.getParticipantLimit())
                .requestModeration(dto.getRequestModeration())
                .title(dto.getTitle())
                .build();
    }

    public static EventShortDto toShortDto(Event e, long views, long confirmed) {
        return EventShortDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(CategoryMapper.toDto(e.getCategory()))
                .eventDate(e.getEventDate())
                .initiator(UserMapper.toShort(e.getInitiator()))
                .paid(e.getPaid())
                .title(e.getTitle())
                .views(views)
                .confirmedRequests(confirmed)
                .build();
    }

    public static EventFullDto toFullDto(Event e, long views, long confirmed) {
        return EventFullDto.builder()
                .id(e.getId())
                .annotation(e.getAnnotation())
                .category(CategoryMapper.toDto(e.getCategory()))
                .description(e.getDescription())
                .eventDate(e.getEventDate())
                .location(e.getLocation() == null ? null
                        : new Location(e.getLocation().getLat(), e.getLocation().getLon()))
                .paid(e.getPaid())
                .participantLimit(e.getParticipantLimit())
                .requestModeration(e.getRequestModeration())
                .title(e.getTitle())
                .initiator(UserMapper.toShort(e.getInitiator()))
                .state(e.getState())
                .createdOn(e.getCreatedOn())
                .publishedOn(e.getPublishedOn())
                .views(views)
                .confirmedRequests(confirmed)
                .build();
    }
}
