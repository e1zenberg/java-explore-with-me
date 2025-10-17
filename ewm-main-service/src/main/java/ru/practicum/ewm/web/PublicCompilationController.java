package ru.practicum.ewm.web;

import java.util.List;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.dto.CompilationDto;
import ru.practicum.ewm.service.CompilationService;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequestMapping("/compilations")
public class PublicCompilationController {

    CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> list(@RequestParam(required = false) Boolean pinned,
                                     @RequestParam(defaultValue = "0") Integer from,
                                     @RequestParam(defaultValue = "10") Integer size) {
        return compilationService.list(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto get(@PathVariable Long compId) {
        return compilationService.getById(compId);
    }
}
