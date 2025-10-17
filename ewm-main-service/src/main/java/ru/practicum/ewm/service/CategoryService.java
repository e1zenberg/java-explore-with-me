package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.dto.CategoryDto;
import ru.practicum.ewm.dto.NewCategoryDto;
import ru.practicum.ewm.error.ConflictException;
import ru.practicum.ewm.error.NotFoundException;
import ru.practicum.ewm.mapper.CategoryMapper;
import ru.practicum.ewm.model.Category;
import ru.practicum.ewm.repo.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class CategoryService {

    CategoryRepository categoryRepository;

    public CategoryDto create(NewCategoryDto dto) {
        if (categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("Категория с таким названием уже существует: " + dto.getName());
        }
        Category saved = categoryRepository.save(CategoryMapper.toEntity(dto));
        return CategoryMapper.toDto(saved);
    }

    public CategoryDto update(Long id, NewCategoryDto dto) {
        Category c = getOr404(id);
        if (!c.getName().equalsIgnoreCase(dto.getName()) && categoryRepository.existsByNameIgnoreCase(dto.getName())) {
            throw new ConflictException("Категория с таким названием уже существует: " + dto.getName());
        }
        c.setName(dto.getName());
        return CategoryMapper.toDto(categoryRepository.save(c));
    }

    public void delete(Long id) {
        categoryRepository.delete(getOr404(id));
    }

    public CategoryDto getById(Long id) {
        return CategoryMapper.toDto(getOr404(id));
    }

    public List<CategoryDto> getAll(Pageable pageable) {
        return categoryRepository.findAll(pageable).getContent().stream().map(CategoryMapper::toDto).toList();
    }

    public Category getOr404(Long id) {
        return categoryRepository.findById(id).orElseThrow(() -> new NotFoundException("Категория не найдена: " + id));
    }
}
