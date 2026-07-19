package ru.yandex.practicum.filmorate.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.dto.filmdto.GenresDto;
import ru.yandex.practicum.filmorate.model.Genres;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GenresMapper {

    public static GenresDto mapToGenresDto(Genres genres) {
        GenresDto genresDto = new GenresDto();
        genresDto.setId(genres.getId());
        genresDto.setName(genres.getName());
        return genresDto;
    }
}
