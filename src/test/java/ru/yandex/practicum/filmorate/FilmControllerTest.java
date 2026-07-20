package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.dto.filmdto.FilmDto;
import ru.yandex.practicum.filmorate.exceptions.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class FilmControllerTest {

    @Autowired
    private FilmController filmController;

    @Autowired
    private UserController userController;
    private Film validFilm;

    @BeforeEach
    void setUp() {

        validFilm = createTestFilm();

    }

    private String uniqueEmail() {
        return "user_" + UUID.randomUUID() + "@test.ru";
    }

    private String uniqueLogin() {
        return "login_" + UUID.randomUUID();
    }

    private String uniqueName() {
        return "User_" + UUID.randomUUID();
    }

    private String uniqueFilmName() {
        return "Film_" + UUID.randomUUID();
    }

    @Test
    void createFilmWithValidDataShouldSetIdAndReturnFilm() {

        Film created =
                filmController.create(validFilm);

        assertNotNull(created.getId());
        assertTrue(created.getId() > 0);
        assertEquals(
                validFilm.getName(),
                created.getName());
        assertEquals(
                validFilm.getDescription(),
                created.getDescription());
        assertEquals(
                validFilm.getReleaseDate(),
                created.getReleaseDate());
        assertEquals(
                validFilm.getDuration(),
                created.getDuration());
    }

    @Test
    void createFilmWithNullDescriptionShouldSucceed() {

        validFilm.setDescription(null);
        Film created =
                filmController.create(validFilm);

        assertNull(created.getDescription());
    }

    @Test
    void createFilmWithBlankDescriptionShouldSucceed() {

        validFilm.setDescription("   ");
        Film created =
                filmController.create(validFilm);

        assertEquals(
                "   ",
                created.getDescription());
    }

    @Test
    void createFilmWithMinReleaseDateShouldSucceed() {

        validFilm.setReleaseDate(
                LocalDate.of(1895,12,28));

        Film created =
                filmController.create(validFilm);

        assertEquals(
                LocalDate.of(1895,12,28),
                created.getReleaseDate());
    }

    @Test
    void createFilmShouldIncrementIds() {

        Film first =
                filmController.create(createTestFilm());
        Film second =
                filmController.create(createTestFilm());

        assertTrue(
                first.getId() < second.getId());
    }

    @Test
    void updateFilmWithValidDataShouldUpdateFields() {

        Film created =
                filmController.create(validFilm);

        created.setName(
                uniqueFilmName());

        created.setDescription(
                "New description");
        created.setDuration(150);

        Film updated =
                filmController.update(created);

        assertEquals(
                created.getName(),
                updated.getName());

        assertEquals(
                created.getDescription(),
                updated.getDescription());

        assertEquals(
                150,
                updated.getDuration());
    }

    @Test
    void updateFilmWithNullIdShouldThrow() {

        validFilm.setId(null);

        assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.update(validFilm));
    }

    @Test
    void updateFilmWithNonExistingIdShouldThrow() {

        validFilm.setId(999999L);
        assertThrows(
                NotFoundException.class,
                () -> filmController.update(validFilm));
    }

    @Test
    void updateFilmWithZeroDurationShouldThrow() {

        Film created =
                filmController.create(validFilm);

        created.setDuration(0);

        assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.update(created));
    }



    @Test
    void updateFilmWithNegativeDurationShouldThrow() {


        Film created =
                filmController.create(validFilm);

        created.setDuration(-10);
        assertThrows(
                ConditionsNotMetException.class,
                () -> filmController.update(created)
        );
    }

    @Test
    void likeFilmShouldThrowWhenFilmNotFound() {

        User user =
                userController.create(createTestUser());

        assertThrows(
                NotFoundException.class,
                () -> filmController.likeFilm(
                        999999L,
                        user.getId()
                ));
    }

    @Test
    void likeFilmShouldThrowWhenUserNotFound() {

        Film film =
                filmController.create(createTestFilm());

        assertThrows(
                NotFoundException.class,
                () -> filmController.likeFilm(
                        film.getId(),
                        999999L
                ));
    }

    @Test
    void getPopularFilmsShouldReturnFilms() {

        Film first =
                filmController.create(createTestFilm());

        Film second =
                filmController.create(createTestFilm());

        List<FilmDto> films =
                filmController.getPopularFilms(5);

        assertNotNull(films);
        assertTrue(
                films.size() >= 2);
    }

    @Test
    void getPopularFilmsShouldReturnEmptyCollectionWhenNoFilms() {

        List<FilmDto> films =
                filmController.getPopularFilms(10);

        assertNotNull(films);
    }

    private User createTestUser() {

        User user = new User();

        user.setEmail(
                uniqueEmail());
        user.setLogin(
                uniqueLogin());
        user.setName(
                uniqueName());
        user.setBirthday(
                LocalDate.of(1990,1,1));

        return user;
    }

    private Film createTestFilm() {

        Film film = new Film();

        film.setName(
                uniqueFilmName());

        film.setDescription(
                "Description_" + UUID.randomUUID());

        film.setReleaseDate(
                LocalDate.of(2000,1,1));

        film.setDuration(120);
        Mpa mpa = new Mpa();
        mpa.setId(1L);
        mpa.setName("G");
        film.setMpa(mpa);
        return film;
    }
}