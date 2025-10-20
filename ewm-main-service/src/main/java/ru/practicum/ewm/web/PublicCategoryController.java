package ru.practicum.ewm.web;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.service.CategoryService;
import ru.practicum.ewm.util.PageUtils;

@RestController
@RequiredArgsConstructor
@Validated
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/categories")
public class PublicCategoryController {

    CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> list(@RequestParam(defaultValue = "0") @Min(0) Integer from,
                                  @RequestParam(defaultValue = "10") @Positive Integer size) {
        return categoryService.getAll(PageUtils.offsetPage(from, size, Sort.by("id").ascending()));
    }

    @GetMapping("/{catId}")
    public CategoryDto get(@PathVariable Long catId) {
        return categoryService.getById(catId);
    }
}
