package ru.practicum.ewm.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 2000)
    String annotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Column(nullable = false, length = 7000)
    String description;

    @Column(nullable = false)
    LocalDateTime eventDate;

    @Embedded
    EventLocation location;

    @Column(nullable = false)
    Boolean paid;

    @Column(nullable = false)
    Integer participantLimit;

    @Column(nullable = false)
    Boolean requestModeration;

    @Column(nullable = false, length = 120)
    String title;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "initiator_id", nullable = false)
    User initiator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    EventState state;

    @Column(nullable = false)
    LocalDateTime createdOn;

    LocalDateTime publishedOn;
}
