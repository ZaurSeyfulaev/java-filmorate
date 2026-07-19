package ru.yandex.practicum.filmorate.dto.filmdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.Genres;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public class FilmDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    private Mpa mpa;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private int duration;
    @JsonProperty("genres")
    private List<Genres> genres;
    private Set<Long> likes;

}
