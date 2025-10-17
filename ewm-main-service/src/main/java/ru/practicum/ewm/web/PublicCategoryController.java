package ru.practicum.ewm.web;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.util.PageUtils;

import java.util.List;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/categories")
public class PublicCategoryController {

    CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> list(@RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "10") Integer size) {
        return categoryService.getAll(PageUtils.offsetPage(from, size, Sort.by("id").ascending()));
    }

    @GetMapping("/{catId}")
    public CategoryDto get(@PathVariable Long catId) {
        return categoryService.getById(catId);
    }
}
