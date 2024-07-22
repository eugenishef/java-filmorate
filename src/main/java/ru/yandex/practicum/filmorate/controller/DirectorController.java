package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.dao.DirectorService;

import javax.validation.Valid;
import java.util.Collection;

@RequestMapping(DirectorController.DIRECTOR_BASE_PATH)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequiredArgsConstructor
@Validated
public class DirectorController {
    public static final String DIRECTOR_BASE_PATH = "/directors";
    final DirectorService directorService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DirectorDto create(@Valid @RequestBody Director newDirector) {
        return directorService.create(newDirector);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<DirectorDto> findAll() {
        return directorService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DirectorDto findDirectorById(@PathVariable @Min(1) Integer id) {
        return directorService.findDirectorById(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public DirectorDto update(@Valid @RequestBody Director updDirector) {
        return directorService.update(updDirector);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable @Min(1) Integer id) {
        directorService.delete(id);
    }
}