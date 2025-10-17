package ru.practicum.ewm.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageUtils {

    private PageUtils() {
    }

    public static Pageable offsetPage(int from, int size, Sort sort) {
        int page = from / size;
        return PageRequest.of(page, size, sort == null ? Sort.unsorted() : sort);
    }
}
