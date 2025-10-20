package ru.practicum.ewm.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageUtils {

    private static final int DEFAULT_SIZE = 10;

    private PageUtils() {
    }

    public static Pageable offsetPage(int from, int size, Sort sort) {
        int effectiveFrom = Math.max(0, from);
        int effectiveSize = size > 0 ? size : DEFAULT_SIZE;
        int page = effectiveFrom / effectiveSize;
        Sort actualSort = (sort == null) ? Sort.unsorted() : sort;
        return PageRequest.of(page, effectiveSize, actualSort);
    }
}
