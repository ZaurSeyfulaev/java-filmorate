package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Autowired
    private UserController userController;

    private Film validFilm;

    @BeforeEach
    void setUp() {

        validFilm = new Film();
        validFilm.setName("Valid Film");
        validFilm.setDescription("Good description");
        validFilm.setReleaseDate(LocalDate.of(2000, 1, 1));
        validFilm.setDuration(120);

    }

    @DirtiesContext
    @Test
    void createFilmWithValidDataShouldSetIdAndReturnFilm() {
        Film created = filmController.create(validFilm);

        assertNotNull(created.getId());
        assertEquals(1L, created.getId());
        assertEquals("Valid Film", created.getName());
        assertEquals("Good description", created.getDescription());
        assertEquals(LocalDate.of(2000, 1, 1), created.getReleaseDate());
        assertEquals(120, created.getDuration());
    }

    @DirtiesContext
    @Test
    void createFilmWithNullDescriptionShouldSucceed() {
        validFilm.setDescription(null);
        Film created = filmController.create(validFilm);
        assertNull(created.getDescription());
    }

    @DirtiesContext
    @Test
    void createFilmWithBlankDescriptionShouldSucceed() {
        validFilm.setDescription("   ");
        Film created = filmController.create(validFilm);
        assertEquals("   ", created.getDescription());
    }

    @DirtiesContext
    @Test
    void createFilmWithReleaseDateExactlyMin_ShouldSucceed() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 28));
        Film created = filmController.create(validFilm);
        assertEquals(LocalDate.of(1895, 12, 28), created.getReleaseDate());
    }

    @DirtiesContext
    @Test
    void createFilmShouldIncrementIds() {
        Film first = filmController.create(validFilm);
        Film second = new Film();
        second.setName("Second");
        second.setDescription("Desc");
        second.setReleaseDate(LocalDate.of(2000, 1, 1));
        second.setDuration(90);
        Film createdSecond = filmController.create(second);

        assertEquals(1L, first.getId());
        assertEquals(2L, createdSecond.getId());
    }

    @DirtiesContext
    @Test
    void updateFilmWithValidDataShouldUpdateFields() {
        Film created = filmController.create(validFilm);
        created.setName("New Name");
        created.setDescription("New desc");
        created.setReleaseDate(LocalDate.of(2020, 1, 1));
        created.setDuration(150);

        Film updated = filmController.update(created);

        assertEquals("New Name", updated.getName());
        assertEquals("New desc", updated.getDescription());
        assertEquals(LocalDate.of(2020, 1, 1), updated.getReleaseDate());
        assertEquals(150, updated.getDuration());
    }

    @DirtiesContext
    @Test
    void updateFilmWithBlankDescriptionShouldUpdateDescriptionToBlank() {
        Film created = filmController.create(validFilm);
        created.setDescription("   ");
        Film updated = filmController.update(created);
        assertEquals("   ", updated.getDescription());
    }

    @DirtiesContext
    @Test
    void createFilmWithNullNameShouldThrow() {
        validFilm.setName(null);
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(validFilm));
    }

    @DirtiesContext
    @Test
    void createFilmWithBlankNameShouldThrow() {
        validFilm.setName("");
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(validFilm));
        validFilm.setName("   ");
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(validFilm));
    }

    @DirtiesContext
    @Test
    void createFilmWithDescriptionLongerThan200ShouldThrow() {
        String longDesc = "a".repeat(201);
        validFilm.setDescription(longDesc);
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(validFilm));
    }

    @DirtiesContext
    @Test
    void createFilmWithReleaseDateBeforeMinShouldThrow() {
        validFilm.setReleaseDate(LocalDate.of(1895, 12, 27)); // на день раньше
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(validFilm));
    }

    @DirtiesContext
    @Test
    void createFilmWithZeroDurationShouldThrow() {
        validFilm.setDuration(0);
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(validFilm));
    }

    @DirtiesContext
    @Test
    void createFilmWithNegativeDurationShouldThrow() {
        validFilm.setDuration(-10);
        assertThrows(ConditionsNotMetException.class, () -> filmController.create(validFilm));
    }

    @DirtiesContext
    @Test
    void updateFilmWithNullIdShouldThrow() {
        validFilm.setId(null);
        assertThrows(ConditionsNotMetException.class, () -> filmController.update(validFilm));
    }

    @DirtiesContext
    @Test
    void updateFilmWithNonExistentIdShouldThrowNotFoundException() {
        validFilm.setId(999L);
        assertThrows(NotFoundException.class, () -> filmController.update(validFilm));
    }

    @DirtiesContext
    @Test
    void updateFilmWithDescriptionLongerThan200ShouldThrow() {
        Film created = filmController.create(validFilm);
        created.setDescription("a".repeat(201));
        assertThrows(ConditionsNotMetException.class, () -> filmController.update(created));
    }

    @DirtiesContext
    @Test
    void updateFilmWithReleaseDateBeforeMinShouldThrow() {
        Film created = filmController.create(validFilm);
        created.setReleaseDate(LocalDate.of(1895, 12, 27));
        assertThrows(ConditionsNotMetException.class, () -> filmController.update(created));
    }

    @DirtiesContext
    @Test
    void updateFilmWithZeroDurationShouldThrow() {
        Film created = filmController.create(validFilm);
        created.setDuration(0);
        assertThrows(ConditionsNotMetException.class, () -> filmController.update(created));
    }

    @DirtiesContext
    @Test
    void updateFilmWithNegativeDurationShouldThrow() {
        Film created = filmController.create(validFilm);
        created.setDuration(-5);
        assertThrows(ConditionsNotMetException.class, () -> filmController.update(created));
    }

    @DirtiesContext
    @Test
    void likeFilmShouldThrowNotFoundExceptionWhenFilmNotFound() {
        User user = createTestUser();
        User createdUser = userController.create(user);
        Long nonExistentFilmId = 999L;

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> filmController.likeFilm(nonExistentFilmId, createdUser.getId()));
        assertTrue(ex.getMessage().contains("Фильм не найден"));
    }

    @DirtiesContext
    @Test
    void likeFilmShouldThrowNotFoundExceptionWhenUserNotFound() {
        Film film = createTestFilm();
        Film createdFilm = filmController.create(film);
        Long nonExistentUserId = 999L;

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> filmController.likeFilm(createdFilm.getId(), nonExistentUserId));
        assertTrue(ex.getMessage().contains("не найден")); // сообщение из userStorage.getUserById
    }

    @DirtiesContext
    @Test
    void deleteFilmShouldThrowNotFoundExceptionWhenFilmNotFound() {
        User user = createTestUser();
        User createdUser = userController.create(user);
        Long nonExistentFilmId = 999L;

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> filmController.deleteFilm(nonExistentFilmId, createdUser.getId()));
        assertTrue(ex.getMessage().contains("Фильм не найден"));
    }

    @DirtiesContext
    @Test
    void deleteFilmShouldThrowNotFoundExceptionWhenUserNotFound() {
        Film film = createTestFilm();
        Film createdFilm = filmController.create(film);
        Long nonExistentUserId = 999L;

        NotFoundException ex = assertThrows(NotFoundException.class,
                () -> filmController.deleteFilm(createdFilm.getId(), nonExistentUserId));
        assertTrue(ex.getMessage().contains("не найден"));
    }


    @DirtiesContext
    @Test
    void getPopularFilmsShouldReturnEmptyCollectionWhenNoFilms() {
        Collection<Film> popular = filmController.getPopularFilms(10);
        assertTrue(popular.isEmpty());
    }

    @DirtiesContext
    @Test
    void getPopularFilmsShouldReturnAllFilmsWhenCountExceedsTotal() {
        // Создаём 2 фильма
        Film f1 = filmController.create(createTestFilm());
        Film f2 = filmController.create(createTestFilm());

        Collection<Film> popular = filmController.getPopularFilms(5);
        assertEquals(2, popular.size());
    }

    // Вспомогательные методы
    private User createTestUser() {
        User user = new User();
        user.setEmail("test@example.com");
        user.setLogin("testLogin");
        user.setName("Test User");
        user.setBirthday(LocalDate.of(1990, 1, 1));
        return user;
    }

    private Film createTestFilm() {
        Film film = new Film();
        film.setName("Test Film");
        film.setDescription("Description");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(120);
        return film;
    }
}