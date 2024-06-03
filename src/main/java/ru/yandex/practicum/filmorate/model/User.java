package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.*;

import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    @Email(message = "Электронная почта должна быть корректной и содержать символ @")
    private String email;
    @NotBlank(message = "Логин не может быть пустым")
    @Pattern(regexp = "\\S+", message = "Логин не должен содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения не может быть в будущем")
    private LocalDate birthday;
}
