package ru.yandex.practicum.filmorate.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.film.GenresDbStorage;
import ru.yandex.practicum.filmorate.dto.filmdto.GenresDto;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.mapper.GenresMapper;
import ru.yandex.practicum.filmorate.model.Genres;

import java.util.List;

@Service
public class GenresService {


    private final GenresDbStorage genresDbStorage;

    public GenresService(GenresDbStorage genresDbStorage) {
        this.genresDbStorage = genresDbStorage;
    }

    public List<GenresDto> getGenres(){
        return genresDbStorage.selectAllGenreTypes().stream()
                .map(GenresMapper::mapToGenresDto)
                .toList();
    }

    public GenresDto getGenresById(Long id){
        Genres genres =  genresDbStorage.selectGenreTypeById(id).orElseThrow(()->
                new NotFoundException("Жанр с id = " + id + " не найден"));
        return GenresMapper.mapToGenresDto(genres);
    }
}
