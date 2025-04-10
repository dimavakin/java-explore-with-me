package ru.practicum.service.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.compilation.CompilationDto;
import ru.practicum.dto.compilation.NewCompilationDto;
import ru.practicum.dto.compilation.UpdateCompilationRequest;
import ru.practicum.dto.event.EventShortDto;
import ru.practicum.exception.NotFoundException;
import ru.practicum.mapper.CompilationMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.model.Compilation;
import ru.practicum.model.Event;
import ru.practicum.repository.CompilationRepository;
import ru.practicum.repository.EventRepository;


import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class AdminCompilationServiceImpl implements AdminCompilationService {
    private final EventRepository eventRepository;
    private final CompilationRepository compilationRepository;

    @Transactional
    @Override
    public CompilationDto postCompilation(NewCompilationDto newCompilationDto) {
        Compilation compilation = CompilationMapper.toCompilationFromNewCompilationDto(newCompilationDto);

        List<Event> events = new ArrayList<>();
        if (newCompilationDto.getEvents() != null && !newCompilationDto.getEvents().isEmpty()) {
            events = eventRepository.findAllById(newCompilationDto.getEvents());
            compilation.setEvents(events);
        } else {
            compilation.setEvents(List.of());
        }

        Compilation saved = compilationRepository.save(compilation);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .toList();

        return CompilationMapper.toCompilationDto(saved, eventShortDtos);
    }

    @Transactional
    @Override
    public ResponseEntity<Void> deleteCompilation(Long compId) {
        if (compilationRepository.existsById(compId)) {
            compilationRepository.deleteById(compId);
        } else {
            throw new NotFoundException(String.format("Compilation with id=%d was not found", compId));
        }
        return ResponseEntity.noContent().build();
    }

    @Transactional
    @Override
    public CompilationDto patchCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException("Compilation with id=" + compId + " not found"));

        List<Event> events = compilation.getEvents();
        if (updateCompilationRequest.getEvents() != null && !updateCompilationRequest.getEvents().isEmpty()) {
            events = eventRepository.findAllById(updateCompilationRequest.getEvents());
            compilation.setEvents(events);
        }

        updateIfPresent(updateCompilationRequest.getTitle(), compilation::setTitle);
        updateIfPresent(updateCompilationRequest.getPinned(), compilation::setPinned);

        Compilation updated = compilationRepository.save(compilation);

        List<EventShortDto> eventShortDtos = events.stream()
                .map(EventMapper::toEventShortDtoFromEvent)
                .toList();

        return CompilationMapper.toCompilationDto(updated, eventShortDtos);
    }

    private <T> void updateIfPresent(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }
}
