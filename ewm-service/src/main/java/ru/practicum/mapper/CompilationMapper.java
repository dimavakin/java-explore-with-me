package ru.practicum.mapper;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.exception.ValidationException;
import ru.practicum.model.Compilation;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation) {
        if (compilation == null) {
            throw new ValidationException("compilation can not be null");
        }
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setEvents(compilation.getEvents());
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());
        return dto;
    }

    public static Compilation toCompilationFromNewCompilationDto(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            throw new ValidationException("compilation can not be null");
        }
        Compilation compilation = new Compilation();
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }
}
