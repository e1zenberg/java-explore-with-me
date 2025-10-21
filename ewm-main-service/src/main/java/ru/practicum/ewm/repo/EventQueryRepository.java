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
    private EntityManager em;

    public List<Event> searchPublic(String text,
                                    List<Long> categories,
                                    Boolean paid,
                                    LocalDateTime rangeStart,
                                    LocalDateTime rangeEnd,
                                    Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> root = cq.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("state"), EventState.PUBLISHED));

        if (text != null && !text.isBlank()) {
            String pattern = "%" + text.toLowerCase() + "%";
            Predicate byAnnotation = cb.like(cb.lower(root.get("annotation")), pattern);
            Predicate byDescription = cb.like(cb.lower(root.get("description")), pattern);
            predicates.add(cb.or(byAnnotation, byDescription));
        }
        if (paid != null) {
            predicates.add(cb.equal(root.get("paid"), paid));
        }
        applyDateAndCategoryFilters(cb, root, predicates, categories, rangeStart, rangeEnd);

        cq.where(predicates.toArray(new Predicate[0]));
        applySorting(cb, cq, root, pageable, "eventDate");

        TypedQuery<Event> q = em.createQuery(cq);
        applyPaging(q, pageable);
        return q.getResultList();
    }

    public List<Event> searchAdmin(List<Long> users,
                                   List<EventState> states,
                                   List<Long> categories,
                                   LocalDateTime rangeStart,
                                   LocalDateTime rangeEnd,
                                   Pageable pageable) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Event> cq = cb.createQuery(Event.class);
        Root<Event> root = cq.from(Event.class);

        List<Predicate> predicates = new ArrayList<>();

        if (users != null && !users.isEmpty()) {
            predicates.add(root.get("initiator").get("id").in(users));
        }
        if (states != null && !states.isEmpty()) {
            predicates.add(root.get("state").in(states));
        }
        applyDateAndCategoryFilters(cb, root, predicates, categories, rangeStart, rangeEnd);

        cq.where(predicates.toArray(new Predicate[0]));
        applySorting(cb, cq, root, pageable, "id");

        TypedQuery<Event> q = em.createQuery(cq);
        applyPaging(q, pageable);
        return q.getResultList();
    }

    private void applyDateAndCategoryFilters(CriteriaBuilder cb,
                                             Root<Event> root,
                                             List<Predicate> predicates,
                                             List<Long> categories,
                                             LocalDateTime rangeStart,
                                             LocalDateTime rangeEnd) {
        if (categories != null && !categories.isEmpty()) {
            predicates.add(root.get("category").get("id").in(categories));
        }
        if (rangeStart != null) {
            predicates.add(cb.greaterThanOrEqualTo(root.get("eventDate"), rangeStart));
        }
        if (rangeEnd != null) {
            predicates.add(cb.lessThanOrEqualTo(root.get("eventDate"), rangeEnd));
        }
    }

    private void applySorting(CriteriaBuilder cb,
                              CriteriaQuery<Event> cq,
                              Root<Event> root,
                              Pageable pageable,
                              String defaultProperty) {
        Sort sort = pageable == null ? Sort.unsorted() : pageable.getSort();
        if (sort.isSorted()) {
            List<jakarta.persistence.criteria.Order> orders = new ArrayList<>();
            for (Sort.Order o : sort) {
                orders.add(o.isAscending() ? cb.asc(root.get(o.getProperty())) : cb.desc(root.get(o.getProperty())));
            }
            cq.orderBy(orders.toArray(new jakarta.persistence.criteria.Order[0]));
        } else {
            cq.orderBy(cb.asc(root.get(defaultProperty)));
        }
    }

    private void applyPaging(TypedQuery<?> q, Pageable pageable) {
        if (pageable != null && pageable.isPaged()) {
            q.setFirstResult((int) pageable.getOffset());
            q.setMaxResults(pageable.getPageSize());
        }
    }
}
