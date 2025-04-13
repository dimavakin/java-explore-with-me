package ru.practicum.mapper;

import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.model.Compilation;

import java.util.List;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtos) {
        if (compilation == null) {
            return null;
        }
        CompilationDto dto = new CompilationDto();
        dto.setId(compilation.getId());
        dto.setEvents(eventShortDtos);
        dto.setPinned(compilation.getPinned());
        dto.setTitle(compilation.getTitle());
        return dto;
    }

    public static Compilation toCompilationFromNewCompilationDto(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            return null;
        }
        Compilation compilation = new Compilation();
        if (newCompilationDto.getPinned() != null) {
            compilation.setPinned(newCompilationDto.getPinned());
        }
        compilation.setTitle(newCompilationDto.getTitle());
        return compilation;
    }
}
