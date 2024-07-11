package ru.yandex.practicum.filmorate.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.filmorate.dal.mappers.UserRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;

@Configuration
public class AppConfig {
    @Bean
    public FilmRowMapper filmRowMapper() {
        return new FilmRowMapper();
    }

    @Bean
    public UserRowMapper userRowMapper() {
        return new UserRowMapper();
    }
}
