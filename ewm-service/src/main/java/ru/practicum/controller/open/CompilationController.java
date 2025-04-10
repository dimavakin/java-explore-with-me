package ru.practicum.controller.open;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.hit.HitRequest;
import ru.practicum.service.open.CompilationService;
import ru.practicum.stats.client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
public class CompilationController {
    private final StatsClient statsClient;
    private static final String APP_NAME = "ewm-main-service";
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "10") Integer size,
                                                HttpServletRequest request) {
        HitRequest hitRequest = new HitRequest(APP_NAME, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        statsClient.saveHit(hitRequest);

        return compilationService.getCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getCompilation(@PathVariable Long compId, HttpServletRequest request) {
        HitRequest hitRequest = new HitRequest(APP_NAME, request.getRequestURI(), request.getRemoteAddr(), LocalDateTime.now());
        statsClient.saveHit(hitRequest);

        return compilationService.getCompilation(compId);
    }
}
