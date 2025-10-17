package ru.practicum.ewm.repo;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;

public interface EventRepository extends JpaRepository<Event, Long> {

    List<Event> findAllByInitiator_Id(Long initiatorId, Pageable pageable);

    @Query("""
        select e
        from Event e
        where e.state = ru.practicum.ewm.model.EventState.PUBLISHED
          and (:text is null or lower(e.annotation) like lower(concat('%', :text, '%'))
               or lower(e.description) like lower(concat('%', :text, '%')))
          and (:paid is null or e.paid = :paid)
          and e.eventDate between :start and :end
        """)
    List<Event> searchPublicNoCategories(String text,
                                         Boolean paid,
                                         LocalDateTime start,
                                         LocalDateTime end,
                                         Pageable pageable);

    @Query("""
        select e
        from Event e
        where e.state = ru.practicum.ewm.model.EventState.PUBLISHED
          and (:text is null or lower(e.annotation) like lower(concat('%', :text, '%'))
               or lower(e.description) like lower(concat('%', :text, '%')))
          and (:paid is null or e.paid = :paid)
          and e.category.id in :categories
          and e.eventDate between :start and :end
        """)
    List<Event> searchPublicWithCategories(String text,
                                           Boolean paid,
                                           List<Long> categories,
                                           LocalDateTime start,
                                           LocalDateTime end,
                                           Pageable pageable);

    @Query("""
        select e
        from Event e
        where (:users is null or e.initiator.id in :users)
          and (:states is null or e.state in :states)
          and (:categories is null or e.category.id in :categories)
          and e.eventDate between :start and :end
        """)
    List<Event> searchAdmin(List<Long> users,
                            List<EventState> states,
                            List<Long> categories,
                            LocalDateTime start,
                            LocalDateTime end,
                            Pageable pageable);
}
