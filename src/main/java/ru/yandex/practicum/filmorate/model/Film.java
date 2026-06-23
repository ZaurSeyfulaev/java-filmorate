package ru.yandex.practicum.filmorate.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id")
@ToString
public class Film {
    Set<Long> likes = new HashSet<>();
    Long id;
    String name;
    String description;
    LocalDate releaseDate;
    Integer duration;
}
