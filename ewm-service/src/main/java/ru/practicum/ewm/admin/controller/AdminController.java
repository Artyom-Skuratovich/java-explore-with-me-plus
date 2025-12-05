package ru.practicum.ewm.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.service.AdminCategoryService;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminCategoryService categoryService;

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto createCategory(@Valid @RequestBody NewCategoryDto category) {
        return categoryService.create(category);
    }

    @DeleteMapping("/categories/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategory(@PathVariable long id) {
        categoryService.delete(id);
    }

    @PatchMapping("categories/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CategoryDto updateCategory(
            @PathVariable long id,
            @Valid @RequestBody NewCategoryDto category) {
        return categoryService.update(id, category);
    }
}