package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class FilmControllerTest {

    private FilmController controller;
    private Film validFilm;

    @BeforeEach
    void setUp() {
        controller = new FilmController();
        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("Good description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);
    }

    @Test
    void createFilmWithValidDataShouldSetIdAndReturnFilm() {
        Film created = controller.create(validFilm);

        assertNotNull(created.getId());
        assertEquals(1L, created.getId());
        assertEquals("Valid Film", created.getName());
        assertEquals("Good description", created.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), created.getReleaseDate());
        assertEquals(120, created.getDuration());
    }

    @Test
    void createFilmWithNullDescriptionShouldSucceed() {
        validFilm.setDescription(null);
        Film created = controller.create(validFilm);
        assertNull(created.getDescription());
    }

    @Test
    void createFilmWithBlankDescriptionShouldSucceed() {
        validFilm.setDescription("   ");
        Film created = controller.create(validFilm);
        assertEquals("   ", created.getDescription());
    }

    @Test
    void createFilmWithReleaseDateExactlyMin_ShouldSucceed() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        Film created = controller.create(validFilm);
        assertEquals(LocalDate.of(1895, 12, 28), created.getReleaseDate());
    }

    @Test
    void createFilmShouldIncrementIds() {
        Film first = controller.create(validFilm);
        Film second = new Film();
        second.setName("Second");
        second.setDescription("Desc");
        second.setReleaseDate(LocalDate.of(2000, 1, 1));
        second.setDuration(90);
        Film createdSecond = controller.create(second);

        assertEquals(1L, first.getId());
        assertEquals(2L, createdSecond.getId());
    }

    @Test
    void updateFilmWithValidDataShouldUpdateFields() {
        Film created = controller.create(validFilm);
        created.setName("New Name");
        created.setDescription("New desc");
        created.setReleaseDate(LocalDate.of(2020, 1, 1));
        created.setDuration(150);

        Film updated = controller.update(created);

        assertEquals("New Name", updated.getName());
        assertEquals("New desc", updated.getDescription());
        assertEquals(LocalDate.of(2020, 1, 1), updated.getReleaseDate());
        assertEquals(150, updated.getDuration());
    }

    @Test
    void updateFilmWithBlankDescriptionShouldUpdateDescriptionToBlank() {
        Film created = controller.create(validFilm);
        created.setDescription("   ");
        Film updated = controller.update(created);
        assertEquals("   ", updated.getDescription());
    }

    @Test
    void createFilmWithNullNameShouldThrow() {
        validFilm.setName(null);
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validFilm));
    }

    @Test
    void createFilmWithBlankNameShouldThrow() {
        validFilm.setName("");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validFilm));
        validFilm.setName("   ");
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validFilm));
    }

    @Test
    void createFilmWithDescriptionLongerThan200ShouldThrow() {
        String longDesc = "a".repeat(201);
        validFilm.setDescription(longDesc);
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validFilm));
    }

    @Test
    void createFilmWithReleaseDateBeforeMinShouldThrow() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27)); // на день раньше
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validFilm));
    }

    @Test
    void createFilmWithZeroDurationShouldThrow() {
        validFilm.setDuration(0);
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validFilm));
    }

    @Test
    void createFilmWithNegativeDurationShouldThrow() {
        validFilm.setDuration(-10);
        assertThrows(ConditionsNotMetException.class, () -> controller.create(validFilm));
    }

    @Test
    void updateFilmWithNullIdShouldThrow() {
        validFilm.setId(null);
        assertThrows(ConditionsNotMetException.class, () -> controller.update(validFilm));
    }

    @Test
    void updateFilmWithNonExistentIdShouldThrowNotFoundException() {
        validFilm.setId(999L);
        assertThrows(NotFoundException.class, () -> controller.update(validFilm));
    }

    @Test
    void updateFilmWithDescriptionLongerThan200ShouldThrow() {
        Film created = controller.create(validFilm);
        created.setDescription("a".repeat(201));
        assertThrows(ConditionsNotMetException.class, () -> controller.update(created));
    }

    @Test
    void updateFilmWithReleaseDateBeforeMinShouldThrow() {
        Film created = controller.create(validFilm);
        created.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ConditionsNotMetException.class, () -> controller.update(created));
    }

    @Test
    void updateFilmWithZeroDurationShouldThrow() {
        Film created = controller.create(validFilm);
        created.setDuration(0);
        assertThrows(ConditionsNotMetException.class, () -> controller.update(created));
    }

    @Test
    void updateFilmWithNegativeDurationShouldThrow() {
        Film created = controller.create(validFilm);
        created.setDuration(-5);
        assertThrows(ConditionsNotMetException.class, () -> controller.update(created));
    }
}