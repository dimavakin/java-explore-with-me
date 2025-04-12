package ru.practicum.service.admin;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.DuplicatedDataException;
import ru.practicum.exception.BadRequestException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;
import ru.practicum.repository.EventRepository;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AdminCategoryServiceImpl implements AdminCategoryService {
    CategoryRepository categoryRepository;
    EventRepository eventRepository;

    @Transactional
    @Override
    public CategoryDto postCategory(NewCategoryDto newCategoryDto) {
        try {
            Category category = categoryRepository.saveAndFlush(CategoryMapper.toCategoryFromNewCategoryDto(newCategoryDto));
            return CategoryMapper.toCategoryDto(category);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedDataException("Category with this name already exists");
        }
    }

    @Transactional
    @Override
    public void deleteCategory(Integer catId) {
        if (!categoryRepository.existsById(catId)) {
            throw new NotFoundException(String.format("Категория с id=%d не найдена", catId));
        }

        if (eventRepository.existsByCategoryId(catId)) {
            throw new BadRequestException("Невозможно удалить категорию, так как с ней связаны события");
        }

        categoryRepository.deleteById(catId);
    }

    @Transactional
    @Override
    public CategoryDto patchCategory(Integer catId, CategoryDto categoryDto) {
        Category existingCategory = categoryRepository.findById(catId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d was not found", catId)));

        if (categoryDto.getName() != null) {
            existingCategory.setName(categoryDto.getName());
        }
        try {
            Category updatedCategory = categoryRepository.saveAndFlush(existingCategory);
            return CategoryMapper.toCategoryDto(updatedCategory);
        } catch (DataIntegrityViolationException e) {
            throw new DuplicatedDataException("Category with this name already exists");
        }
    }
}
