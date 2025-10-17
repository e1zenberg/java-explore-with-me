package ru.practicum.ewm.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("""
        select e
        from Event e
        where e.state = ru.practicum.ewm.model.EventState.PUBLISHED
          and ( :text is null
                or (lower(e.annotation) like lower(concat('%', :text, '%'))
                    or lower(e.description) like lower(concat('%', :text, '%'))) )
          and ( :paid is null or e.paid = :paid )
          and ( :categories is null or e.category.id in :categories )
          and ( e.eventDate between :start and :end )
        """)
    List<Event> searchPublic(@Param("text") String text,
                             @Param("paid") Boolean paid,
                             @Param("categories") Collection<Long> categories,
                             @Param("start") LocalDateTime start,
                             @Param("end") LocalDateTime end,
                             Pageable pageable);

    @Query("""
        select e
        from Event e
        where ( :users is null or e.initiator.id in :users )
          and ( :states is null or e.state in :states )
          and ( :categories is null or e.category.id in :categories )
          and ( e.eventDate between :start and :end )
        """)
    List<Event> searchAdmin(@Param("users") Collection<Long> users,
                            @Param("states") Collection<EventState> states,
                            @Param("categories") Collection<Long> categories,
                            @Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end,
                            Pageable pageable);

    List<Event> findAllByInitiator_Id(Long initiatorId, Pageable pageable);
}
