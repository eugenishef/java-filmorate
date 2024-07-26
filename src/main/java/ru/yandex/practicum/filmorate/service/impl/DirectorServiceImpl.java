package ru.yandex.practicum.filmorate.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.dao.DirectorStorage;
import ru.yandex.practicum.filmorate.dto.DirectorDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.dao.DirectorService;
import ru.yandex.practicum.filmorate.validation.DirectorValidator;

import java.util.Collection;
import java.util.Optional;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Slf4j
@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    final DirectorStorage directorStorage;

    static final String DIRECTOR_NOT_FOUND_MSG = "Режиссер с id = %d не найден";

    @Override
    public DirectorDto create(Director newDirector) {
        DirectorValidator.validate(newDirector);
        return DirectorMapper.modelToDto(directorStorage.create(newDirector));
    }

    @Override
    public Collection<DirectorDto> findAll() {
        return directorStorage.findAll().stream().map(DirectorMapper::modelToDto).toList();
    }

    @Override
    public DirectorDto findDirectorById(Integer id) {
        return DirectorMapper.modelToDto(directorStorage.findDirectorById(id)
                .orElseThrow(() -> new NotFoundException(String.format(DIRECTOR_NOT_FOUND_MSG, id))));
    }

    @Override
    public DirectorDto update(Director updDirector) {
        DirectorValidator.validate(updDirector);
        Director oldDirector = directorStorage.findDirectorById(updDirector.getId())
                .orElseThrow(() -> {
                    String idNotFound = String.format(DIRECTOR_NOT_FOUND_MSG, updDirector.getId());
                    log.warn(DIRECTOR_NOT_FOUND_MSG, idNotFound);
                    return new NotFoundException(idNotFound);
                });

        oldDirector.setName(Optional.ofNullable(updDirector.getName()).filter(name ->
                !name.isBlank()).orElse(oldDirector.getName()));

        return DirectorMapper.modelToDto(directorStorage.update(updDirector));
    }

    @Override
    public void delete(Integer id) {
        findDirectorById(id);
        directorStorage.delete(id);
    }
}