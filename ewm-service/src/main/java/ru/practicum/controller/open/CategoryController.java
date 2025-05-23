package ru.practicum.controller.open;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.category.CategoryDto;
import ru.practicum.dto.hit.HitRequest;
import ru.practicum.service.open.CategoryService;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryController {
    StatsClient statsClient;
    static String APP_NAME = "ewm-main-service";
    CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from, @RequestParam(defaultValue = "10") Integer size, HttpServletRequest request) {
        HitRequest hitRequest = new HitRequest(APP_NAME, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        statsClient.saveHit(hitRequest);

        return categoryService.getCategories(from, size);
    }

    @GetMapping("/{catId}")
    public CategoryDto getCategory(@PathVariable Integer catId, HttpServletRequest request) {
        HitRequest hitRequest = new HitRequest(APP_NAME, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        statsClient.saveHit(hitRequest);

        return categoryService.getCategory(catId);
    }
}
