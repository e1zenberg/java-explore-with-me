package ru.practicum.ewm.mapper;

import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.model.Category;

public final class CategoryMapper {

    private CategoryMapper() {
    }

    public static CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .build();
    }

    public static Category toEntity(NewCategoryDto dto) {
        return Category.builder()
                .name(dto.getName())
                .build();
    }
}
