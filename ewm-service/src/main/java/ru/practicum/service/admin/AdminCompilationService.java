package ru.practicum.service.admin;

import org.springframework.http.ResponseEntity;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;

public interface AdminCompilationService {
    CompilationDto postCompilation(NewCompilationDto newCompilationDto);

    ResponseEntity<Void> deleteCompilation(Long compId);

    CompilationDto patchCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);
}
