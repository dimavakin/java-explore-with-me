package ru.practicum.mapper;

import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.Category;

public class CategoryMapper {
    public static CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            throw new ValidationException("category can not be null");
        }
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    public static Category toCategoryFromNewCategoryDto(NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            throw new ValidationException("category can not be null");
        }
        Category category = new Category();
        category.setName(newCategoryDto.getName());
        return category;
    }

    public static Category toCategoryFromCategoryDto(CategoryDto CategoryDto, Integer catId) {
        if (CategoryDto == null) {
            throw new ValidationException("category can not be null");
        }
        Category category = new Category();
        category.setId(catId);
        category.setName(CategoryDto.getName());
        return category;
    }
}
