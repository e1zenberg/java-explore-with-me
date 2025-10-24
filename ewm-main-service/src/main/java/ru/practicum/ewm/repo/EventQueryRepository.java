package ru.practicum.ewm.repo;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.model.Event;
import ru.practicum.ewm.model.EventState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public class EventQueryRepository {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Event> searchPublic(String text,
                                    List<Long> categories,
                                    Boolean paid,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(criteriaBuilder.equal(eventRoot.get("state"), EventState.PUBLISHED));

        if (text != null && !text.isBlank()) {
            String pattern = "%" + text.toLowerCase() + "%";
            Predicate byAnnotation = criteriaBuilder.like(criteriaBuilder.lower(eventRoot.get("annotation")), pattern);
            Predicate byDescription = criteriaBuilder.like(criteriaBuilder.lower(eventRoot.get("description")), pattern);
            predicates.add(criteriaBuilder.or(byAnnotation, byDescription));
        }
        if (paid != null) {
            predicates.add(criteriaBuilder.equal(eventRoot.get("paid"), paid));
        }
        applyDateAndCategoryFilters(criteriaBuilder, eventRoot, predicates, categories, rangeStart, rangeEnd);

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        applySorting(criteriaBuilder, criteriaQuery, eventRoot, pageable, "eventDate");

        TypedQuery<Event> query = entityManager.createQuery(criteriaQuery);
        applyPaging(query, pageable);
        return query.getResultList();
    }

    public List<Event> searchAdmin(List<Long> users,
                                   List<EventState> states,
                                   List<Long> categories,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   Pageable pageable) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Event> criteriaQuery = criteriaBuilder.createQuery(Event.class);
        Root<Event> eventRoot = criteriaQuery.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            predicates.add(eventRoot.get("initiator").get("id").in(users));
        }
        if (states != null && !states.isEmpty()) {
            predicates.add(eventRoot.get("state").in(states));
        }
        applyDateAndCategoryFilters(criteriaBuilder, eventRoot, predicates, categories, rangeStart, rangeEnd);

        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        applySorting(criteriaBuilder, criteriaQuery, eventRoot, pageable, "id");

        TypedQuery<Event> query = entityManager.createQuery(criteriaQuery);
        applyPaging(query, pageable);
        return query.getResultList();
    }

    private void applyDateAndCategoryFilters(CriteriaBuilder builder,
                                             Root<Event> root,
                                             List<Predicate> predicates,
                                             List<Long> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd) {
        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categories));
        }
        if (rangeStart != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
    }

    private void applySorting(CriteriaBuilder builder,
                              CriteriaQuery<Event> query,
                              Root<Event> root,
                              Pageable pageable,
                              String defaultProperty) {
        Sort sort = pageable == null ? Sort.unsorted() : pageable.getSort();
        if (sort.isSorted()) {
            List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();
            for (Sort.Order order : sort) {
                orders.add(order.isAscending() ? builder.asc(root.get(order.getProperty()))
                        : builder.desc(root.get(order.getProperty())));
            }
            query.orderBy(orders.toArray(new jakarta.persistence.criteria.Order[0]));
        } else {
            query.orderBy(builder.asc(root.get(defaultProperty)));
        }
    }

    private void applyPaging(TypedQuery<?> query, Pageable pageable) {
        if (pageable != null && pageable.isPaged()) {
            query.setFirstResult((int) pageable.getOffset());
            query.setMaxResults(pageable.getPageSize());
        }
    }
}
