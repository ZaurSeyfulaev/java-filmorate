package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.dto.filmdto.GenresDto;
import ru.yandex.practicum.filmorate.service.GenresService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/genres")
public class GenreTypeController {

    private final GenresService genresService;

    public GenreTypeController(GenresService genresService) {
        this.genresService = genresService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<GenresDto> getGenreTypes() { //готово
        log.info("Вызван метод getGenreTypes()");
        return genresService.getGenres();
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public GenresDto getGenreTypesById(@PathVariable Long id) { //Film
        log.info("Вызван метод getGenreTypesById()");
        return genresService.getGenresById(id);
    }
}
