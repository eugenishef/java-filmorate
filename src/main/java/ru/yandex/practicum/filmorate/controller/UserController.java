package ru.yandex.practicum.filmorate.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.FilmDto;
import ru.yandex.practicum.filmorate.dto.UserDto;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.dao.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.Collections;

@RequestMapping(UserController.USERS_BASE_PATH)
@FieldDefaults(level = AccessLevel.PRIVATE)
@RestController
@RequiredArgsConstructor
@Validated
public class UserController {
    static final String USERS_BASE_PATH = "/users";
    static final String FRIENDS_PATH = "/{id}/friends/{friendId}";
    final UserService userService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> findAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto findUserById(@PathVariable @Min(1) Long id) {
        return userService.findUserById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@Valid @RequestBody User newUser) {
        return userService.create(newUser);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public UserDto update(@Valid @RequestBody User updUser) {
        return userService.update(updUser);
    }

    @PutMapping(FRIENDS_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addFriend(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long friendId) {
        userService.addFriend(id, friendId);
    }

    @DeleteMapping(FRIENDS_PATH)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriend(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long friendId) {
        userService.deleteFriend(id, friendId);
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Collection<UserDto>> getFriends(@PathVariable @Min(1) Long id) {
        try {
            Collection<UserDto> friends = userService.getUserFriends(id);
            return ResponseEntity.ok(friends);
        } catch (NotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public Collection<UserDto> getCommonFriends(@PathVariable @Min(1) Long id, @PathVariable @Min(1) Long otherId) {
        return userService.getCommonFriends(id, otherId);
    }

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public Collection<FilmDto> getRecommendations(@PathVariable @Min(1) Long id) {
        return userService.getRecommendations(id);
    }

}

