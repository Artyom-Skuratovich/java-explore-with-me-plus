package ru.practicum.ewm.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    /**
     * Получение списка категорий с пагинацией
     * @param from начальная позиция (по умолчанию 0)
     * @param size количество элементов на странице (по умолчанию 10)
     * @return список DTO категорий
     * **/
    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                           @RequestParam(defaultValue = "10")  Integer size) {
        return categoryService.getCategories(from, size);
    }

    /**
     * Получение категории по ID
     * @param catId идентификатор категории
     * @return DTO категории
     * **/
    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Long catId) {
        return categoryService.getCategory(catId);
    }

}